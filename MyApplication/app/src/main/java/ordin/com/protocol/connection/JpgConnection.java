package ordin.com.protocol.connection;

import android.util.Log;

import java.net.DatagramSocket;
import java.net.Socket;

import ordin.com.protocol.command.Command;

/**
 * Created by sean on 4/8/15.
 */
public class JpgConnection extends BaseConnection {
    private final static String TAG = "JpgConnection";
    DatagramSocket socket;
    public JpgConnection(DatagramSocket socket) {
        this.socket = socket;
    }
    @Override
    public void release() {
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
    }

    @Override
    public void onStop() {
    }
}
