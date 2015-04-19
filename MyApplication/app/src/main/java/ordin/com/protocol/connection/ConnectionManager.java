package ordin.com.protocol.connection;

import android.content.Context;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by sean on 4/17/15.
 */
public class ConnectionManager {
    private final static String TAG = "ConnectionManager";
    public static ConnectionManager defaultManager = new ConnectionManager();
    private ConnectionManager() {}

    public interface OnGetConnection {
        void onGetControlConnection(ControlConnection con);
        void onGetJpgConnection(JpgConnection con);
    }
    String serviceIpAddress;
    int commandPort, jpgPort;
    OnGetConnection onGetConnection;

    ControlConnection controlConnection;
    JpgConnection jpgConnection;

    public void setOnGetConnection(OnGetConnection onGetConnection) {
        this.onGetConnection = onGetConnection;
    }
    private void onGetServiceInfo(String ipAddress, int commandPort, int jpgPort) {
        this.serviceIpAddress = ipAddress;
        this.commandPort = commandPort;
        this.jpgPort = jpgPort;
    }

    private ServiceDiscoverer.Observer discoveryObserver = new ServiceDiscoverer.Observer() {
        @Override
        public void onDiscoveryFailed() {
        }
        @Override
        public void onDiscoveryStarted() {
        }
        @Override
        public void onDiscoveryService(String ipAddress, int commandPort, int jpgPort) {
            onGetServiceInfo(ipAddress, commandPort, jpgPort);
            // create control connection(TCP)
            try {
                Socket ctrlSocket = new Socket(InetAddress.getByName(ipAddress), commandPort);
                controlConnection = new ControlConnection(ctrlSocket);
                controlConnection.start();
                if(onGetConnection != null) {
                    onGetConnection.onGetControlConnection(controlConnection);
                }

                DatagramSocket jpgSocket = new DatagramSocket(jpgPort, InetAddress.getByName(ipAddress));
                jpgConnection = new JpgConnection(jpgSocket);
                jpgConnection.start();
                if(onGetConnection != null) {
                    onGetConnection.onGetJpgConnection(jpgConnection);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.w(TAG, e.toString());
            }

        }
        @Override
        public void onDiscoveryFinished() {
        }
    };

    public void startDiscovery(Context ctx) {
        ServiceDiscoverer.defaultDiscoverer.addObserver(discoveryObserver);
        ServiceDiscoverer.defaultDiscoverer.setContext(ctx);
        ServiceDiscoverer.defaultDiscoverer.start();
    }

    public void stopDiscovery() {
        ServiceDiscoverer.defaultDiscoverer.removeObserver(discoveryObserver);
        ServiceDiscoverer.defaultDiscoverer.stop();
    }
}
