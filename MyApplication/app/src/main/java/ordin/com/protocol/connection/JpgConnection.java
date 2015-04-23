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
    int port;

    public JpgConnection(int port) {
        this.port = port;
    }

    private void release() {
        readerThread = null;
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

    private final class ReaderThread extends Thread {
        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            try {
                Log.i(TAG, "Reader thread try to bind local port:" + port);
                socket = new DatagramSocket(port);
                Log.i(TAG, "bind to port:" + port);
                synchronized (stateListeners) {
                    for(ConnectionStateListener l : stateListeners) {
                        l.onConnected(JpgConnection.this);
                    }
                }
                while(started) {
                    // Read data packet here;
                    ByteBuffer bb = Command.readOneCommand(socket);
                    Command cmd = ResponseParser.parserResponse(bb);
                    synchronized (cmdListeners) {
                        for(CommandListener l : cmdListeners) {
                            l.onReceivedCommand(cmd);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "exception raised in reader thread:" + e.getMessage());
                JpgConnection.this.stop();
            }
        }
    }
}
