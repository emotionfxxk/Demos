package ordin.com.protocol.connection;

import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
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

    String ipAddress;
    int port;

    public ControlConnection(String ipAddress, int commandPort) {
        this.ipAddress = ipAddress;
        this.port = commandPort;
    }

    private void release() {
        readerThread = null;
        writerThread = null;
        commandsQueue.clear();
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
        if(!started) {
            Log.i(TAG, "connection not started, drop command!");
            return;
        }
        synchronized (commandsQueue) {
            commandsQueue.offer(cmd);
        }
        synchronized (queueLock) {
            queueLock.notifyAll();
        }
    }

    @Override
    public void onStart() {
        synchronized (stateListeners) {
            for(ConnectionStateListener l : stateListeners) {
                l.onConnecting(this);
            }
        }
        readerThread = new ReaderThread();
        readerThread.start();
    }

    @Override
    public void onStop() {
        release();
        synchronized (stateListeners) {
            for(ConnectionStateListener l : stateListeners) {
                l.onDisconnected(this);
            }
        }
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
                        synchronized (queueLock) {
                            queueLock.wait(100);
                        }
                    } else {
                        byte[] data = cmd.getDataPacket();
                        outputStream.write(data);
                        outputStream.flush();
                        // notify
                        synchronized (cmdListeners) {
                            for(CommandListener l : cmdListeners) {
                                l.onSentCommand(cmd);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, "WriterThread exception:" + e.getMessage());
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
                Log.i(TAG, "Reader thread started, try connect to " + ipAddress + ":" + port);
                socket = new Socket(InetAddress.getByName(ipAddress), port);
                Log.i(TAG, "connected to " + ipAddress + ":" + port);
                synchronized (stateListeners) {
                    for(ConnectionStateListener l : stateListeners) {
                        l.onConnected(ControlConnection.this);
                    }
                }
                writerThread = new WriterThread();
                writerThread.start();
                inputStream = socket.getInputStream();
                while(started) {
                    ByteBuffer bb = Command.readOneCommand(inputStream);
                    Command cmd = ResponseParser.parserResponse(bb);
                    synchronized (cmdListeners) {
                        for(CommandListener l : cmdListeners) {
                            l.onReceivedCommand(cmd);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, "reader thread exception:" + e.getMessage());
                ControlConnection.this.stop();
            }
        }
    }
}
