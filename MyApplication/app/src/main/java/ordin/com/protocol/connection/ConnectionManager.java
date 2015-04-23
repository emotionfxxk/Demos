package ordin.com.protocol.connection;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import ordin.com.protocol.command.CommandDefs;
import ordin.com.protocol.command.Request;
import ordin.com.protocol.command.RequestFactory;
import ordin.com.protocol.image.ImageProcessor;
import ordin.com.protocol.image.ImageUpdater;

/**
 * Created by sean on 4/17/15.
 */
public class ConnectionManager {
    public final static int MSG_ON_DISCOVERY_STARTED = 0x00;
    public final static int MSG_ON_DISCOVERY_FAILED = 0x01;
    public final static int MSG_ON_DISCOVERY_SERVICE = 0x02;
    public final static int MSG_ON_DISCOVERY_FINISHED = 0x03;

    public final static int MSG_ON_CTRL_CON_CONNECTING = 0x10;
    public final static int MSG_ON_CTRL_CON_CONNECTED = 0x11;
    public final static int MSG_ON_CTRL_CON_DISCONNECTED = 0x12;

    public final static int MSG_ON_JPG_CON_CONNECTING = 0x20;
    public final static int MSG_ON_JPG_CON_CONNECTED = 0x21;
    public final static int MSG_ON_JPG_CON_DISCONNECTED = 0x22;

    private final static String TAG = "ConnectionManager";
    public static ConnectionManager defaultManager = new ConnectionManager();
    private ConnectionManager() {
        imageProcessor = new ImageProcessor();
        //imageUpdater = new ImageUpdater();
        //imageProcessor.setUpdaterHandler(imageUpdater.handler);
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

    private ConnectionStateListener connectionStateListener = new ConnectionStateListener() {
        @Override
        public void onConnecting(IConnection con) {
            if(con == jpgConnection) {
                if (handler != null) {
                    handler.sendMessage(handler.obtainMessage(MSG_ON_JPG_CON_CONNECTING));
                }
            } else if(con == controlConnection) {
                if (handler != null) {
                    handler.sendMessage(handler.obtainMessage(MSG_ON_CTRL_CON_CONNECTING));
                }
            }
        }

        @Override
        public void onConnected(IConnection con) {
            if(con == jpgConnection) {
                if (handler != null) {
                    handler.sendMessage(handler.obtainMessage(MSG_ON_JPG_CON_CONNECTED));
                }
            } else if(con == controlConnection) {
                if (handler != null) {
                    handler.sendMessage(handler.obtainMessage(MSG_ON_CTRL_CON_CONNECTED));
                }
            }
        }

        @Override
        public void onDisconnected(IConnection con) {
            if(con == controlConnection) {
                controlConnection.removeStateListener(connectionStateListener);
                controlConnection = null;
                if (handler != null) {
                    handler.sendMessage(handler.obtainMessage(MSG_ON_CTRL_CON_DISCONNECTED));
                }
            } else if(con == jpgConnection) {
                jpgConnection.removeStateListener(connectionStateListener);
                jpgConnection.removeCommandListener(imageProcessor);
                imageProcessor.stop();
                jpgConnection = null;
                if (handler != null) {
                    handler.sendMessage(handler.obtainMessage(MSG_ON_JPG_CON_DISCONNECTED));
                }
            }
        }
    };

    private ServiceDiscoverer.Observer discoveryObserver = new ServiceDiscoverer.Observer() {
        @Override
        public void onDiscoveryFailed() {
            Log.i(TAG, "onDiscoveryFailed");
            if (handler != null) {
                handler.sendMessage(handler.obtainMessage(MSG_ON_DISCOVERY_FAILED));
            }
        }
        @Override
        public void onDiscoveryStarted() {
            Log.i(TAG, "onDiscoveryStarted");
            if (handler != null) {
                handler.sendMessage(handler.obtainMessage(MSG_ON_DISCOVERY_STARTED));
            }
        }
        @Override
        public void onDiscoveryService(String ipAddress, int commandPort, int jpgPort) {
            Log.i(TAG, "onDiscoveryService ipAddress:" + ipAddress + ", cmd port:" + commandPort + ", jpgPort:" + jpgPort);
            if (handler != null) {
                handler.sendMessage(handler.obtainMessage(MSG_ON_DISCOVERY_SERVICE));
            }
            onGetServiceInfo(ipAddress, commandPort, jpgPort);

            if(controlConnection == null) {
                controlConnection = new ControlConnection(ipAddress, commandPort);
                controlConnection.addStateListener(connectionStateListener);
                controlConnection.start();
            }

            if(jpgConnection == null) {
                jpgConnection = new JpgConnection(jpgPort);
                jpgConnection.addStateListener(connectionStateListener);
                jpgConnection.start();
            }
        }

        @Override
        public void onDiscoveryFinished() {
            Log.i(TAG, "onDiscoveryFinished");
            if (handler != null) {
                handler.sendMessage(handler.obtainMessage(MSG_ON_DISCOVERY_FINISHED));
            }
        }
    };


    public ControlConnection getControlConnection() {
        return controlConnection;
    }

    public void connect(Handler handler, Context ctx) {
        disconnect();
        this.handler = handler;
        startDiscovery(ctx);
    }

    public void disconnect() {
        cancelDiscovery();
        if(controlConnection != null) {
            controlConnection.stop();
            controlConnection.removeStateListener(connectionStateListener);
            controlConnection = null;
        }
        if(jpgConnection != null) {
            jpgConnection.stop();
            jpgConnection.removeStateListener(connectionStateListener);
            jpgConnection = null;
        }
    }

    public void startJpgTransport(ImageUpdater imageUpdater,
                                  short resolutionX, short resolutionY, byte[] signals) {
        if(jpgConnection == null || controlConnection == null)
            throw new IllegalStateException("JPG transport must be start after JPG connection is established!");

        // setup image updater and jpg connection
        this.imageUpdater = imageUpdater;
        imageProcessor.setUpdaterHandler(imageUpdater.handler);
        imageProcessor.start();
        jpgConnection.addCommandListener(imageProcessor);

        // send start jpg transport request
        Request req = RequestFactory.createJpgRequest(CommandDefs.PARAM_SIGNAL_IMAGE, true,
                resolutionX, resolutionY, signals);
        controlConnection.sendCommand(req);
    }

    public void stopJpgTransport(byte[] signals) {
        if(jpgConnection == null || controlConnection == null)
            throw new IllegalStateException("JPG transport must be start after JPG connection is established!");
        // send stop jpg transport request
        Request req = RequestFactory.createJpgRequest(CommandDefs.PARAM_SIGNAL_IMAGE, false,
                (short)0, (short)0, signals);
        controlConnection.sendCommand(req);
    }

    protected void startDiscovery(Context ctx) {
        ServiceDiscoverer.defaultDiscoverer.addObserver(discoveryObserver);
        ServiceDiscoverer.defaultDiscoverer.setContext(ctx);
        ServiceDiscoverer.defaultDiscoverer.start();
    }

    protected void cancelDiscovery() {
        ServiceDiscoverer.defaultDiscoverer.removeObserver(discoveryObserver);
        ServiceDiscoverer.defaultDiscoverer.stop();
    }
}
