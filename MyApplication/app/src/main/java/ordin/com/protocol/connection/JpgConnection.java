package ordin.com.protocol.connection;

import android.util.Log;

import java.net.DatagramSocket;
import java.nio.ByteBuffer;

import ordin.com.protocol.command.Command;
import ordin.com.protocol.command.ResponseParser;

/**
 * Created by sean on 4/8/15.
 */
public class JpgConnection extends BaseConnection {
    private final static String TAG = "JpgConnection";
    DatagramSocket socket;
    ReaderThread readerThread;
    public JpgConnection(DatagramSocket socket) {
        this.socket = socket;
    }

    private void release() {
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
    }

    @Override
    public void onStart() {
        readerThread = new ReaderThread();
        readerThread.start();
    }

    @Override
    public void onStop() {
        release();
    }

    private final class ReaderThread extends Thread {
        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            try {
                while(started) {
                    // Read data packet here;
                    ByteBuffer bb = Command.readOneCommand(socket);
                    Command cmd = ResponseParser.parserResponse(bb);
                    synchronized (listeners) {
                        for(CommandListener l : listeners) {
                            l.onReceivedCommand(cmd);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                JpgConnection.this.stop();
            }
        }
    }
}
