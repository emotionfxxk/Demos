package ordin.com.protocol.connection;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import ordin.com.protocol.image.ImageProcessor;
import ordin.com.protocol.image.ImageUpdater;

/**
 * Created by sean on 4/17/15.
 */
public class ConnectionManager {
    public final static int MSG_ON_GET_CONTROL_CONNECTION = 0;
    public final static int MSG_ON_GET_JPG_CONNECTION = 1;

    private final static String TAG = "ConnectionManager";
    public static ConnectionManager defaultManager = new ConnectionManager();
    private ConnectionManager() {
        imageProcessor = new ImageProcessor();
        imageUpdater = new ImageUpdater();
        imageProcessor.setUpdaterHandler(imageUpdater.handler);
    }

    String serviceIpAddress;
    int commandPort, jpgPort;

    ControlConnection controlConnection;

    /*
     * jpgConnection --(JpgResponse)--> ImageProcessor --(ImagePacket, repack and decode)--> ImageUpdater
     */
    JpgConnection jpgConnection;
    ImageProcessor imageProcessor;
    ImageUpdater imageUpdater;

    Handler handler;

    private void onGetServiceInfo(String ipAddress, int commandPort, int jpgPort) {
        this.serviceIpAddress = ipAddress;
        this.commandPort = commandPort;
        this.jpgPort = jpgPort;
    }

    private ServiceDiscoverer.Observer discoveryObserver = new ServiceDiscoverer.Observer() {
        @Override
        public void onDiscoveryFailed() {
            Log.i(TAG, "onDiscoveryFailed");
        }
        @Override
        public void onDiscoveryStarted() {
            Log.i(TAG, "onDiscoveryStarted");
        }
        @Override
        public void onDiscoveryService(String ipAddress, int commandPort, int jpgPort) {
            Log.i(TAG, "onDiscoveryService ipAddress:" + ipAddress + ", cmd port:" + commandPort + ", jpgPort:" + jpgPort);
            onGetServiceInfo(ipAddress, commandPort, jpgPort);
            // create control connection(TCP)
            try {
                Socket ctrlSocket = new Socket(InetAddress.getByName(ipAddress), commandPort);
                controlConnection = new ControlConnection(ctrlSocket);
                controlConnection.start();
                if(handler != null) {
                    handler.sendMessage(handler.obtainMessage(MSG_ON_GET_CONTROL_CONNECTION, controlConnection));
                }

                DatagramSocket jpgSocket = new DatagramSocket(jpgPort);
                jpgConnection = new JpgConnection(jpgSocket);
                jpgConnection.start();
                jpgConnection.addCommandListener(imageProcessor);
                imageProcessor.start();
                if(handler != null) {
                    handler.sendMessage(handler.obtainMessage(MSG_ON_GET_JPG_CONNECTION, jpgConnection));
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.w(TAG, e.toString());
            }

        }
        @Override
        public void onDiscoveryFinished() {
            Log.i(TAG, "onDiscoveryFinished");
        }
    };

    public ImageUpdater getImageUpdater() {
        return imageUpdater;
    }

    public void getControlConnection(Handler handler, Context ctx) {
        this.handler = handler;
        if(controlConnection != null) {
            if(handler != null) {
                handler.sendMessage(handler.obtainMessage(MSG_ON_GET_CONTROL_CONNECTION, controlConnection));
            }
        } else {
            startDiscovery(ctx);
        }
    }

    public void getJpgConnection(Handler handler, Context ctx) {
        this.handler = handler;
        if(jpgConnection != null) {
            if(handler != null) {
                handler.sendMessage(handler.obtainMessage(MSG_ON_GET_JPG_CONNECTION, jpgConnection));
            }
        } else {
            startDiscovery(ctx);
        }
    }

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
