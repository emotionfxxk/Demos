package ordin.com.protocol.connection;

import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;

import ordin.com.protocol.command.Command;
import ordin.com.protocol.command.ResponseParser;

/**
 * Created by sean on 4/8/15.
 */
public class ControlConnection extends BaseConnection {
    private final static String TAG = "ControlConnection";
    Socket socket;
    ReaderThread readerThread;
    WriterThread writerThread;
    ArrayDeque<Command> commandsQueue = new ArrayDeque<Command>();
    Object queueLock = new Object();

    public ControlConnection(Socket socket) {
        this.socket = socket;
    }

    private void release() {
        readerThread = null;
        writerThread = null;
        try {
            if (socket != null) {
                socket.close();
                socket = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, e.getMessage());
        }
    }
    @Override
    public void sendCommand(Command cmd) {
        synchronized (commandsQueue) {
            commandsQueue.offer(cmd);
        }
        queueLock.notifyAll();
    }

    @Override
    public void onStart() {
        readerThread = new ReaderThread();
        readerThread.start();
        writerThread = new WriterThread();
        writerThread.start();
    }

    @Override
    public void onStop() {
        release();
    }

    private final class WriterThread extends Thread {
        OutputStream outputStream;
        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            try {
                outputStream = socket.getOutputStream();
                Command cmd;
                while(started) {
                    synchronized (commandsQueue) {
                        cmd = commandsQueue.poll();
                    }
                    if(cmd == null) {
                        queueLock.wait(100);
                    } else {
                        outputStream.write(cmd.getDataPacket());
                        // notify
                        synchronized (listeners) {
                            for(CommandListener l : listeners) {
                                l.onSentCommand(cmd);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                ControlConnection.this.stop();
            }
        }
    }

    private final class ReaderThread extends Thread {
        InputStream inputStream;
        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            try {
                inputStream = socket.getInputStream();
                while(started) {
                    // Read data here;
                    ByteBuffer bb = Command.readOneCommand(inputStream);
                    Command cmd = ResponseParser.parserResponse(bb);
                    synchronized (listeners) {
                        for(CommandListener l : listeners) {
                            l.onReceivedCommand(cmd);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
