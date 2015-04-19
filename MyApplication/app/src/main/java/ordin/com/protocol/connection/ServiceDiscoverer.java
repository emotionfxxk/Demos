package ordin.com.protocol.connection;

import android.content.Context;
import android.os.*;
import android.support.annotation.NonNull;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import ordin.com.protocol.command.Request;
import ordin.com.protocol.command.RequestFactory;
import ordin.com.protocol.command.Response;
import ordin.com.protocol.command.ResponseParser;
import ordin.com.protocol.command.ServiceDiscoverResponse;

/**
 * Discover service in LAN(UDP)
 * Created by sean on 4/8/15.
 */
public class ServiceDiscoverer extends IState {
    private final static String TAG = "ServiceDiscoverer";
    public final static ServiceDiscoverer defaultDiscoverer = new ServiceDiscoverer();
    private ServiceDiscoverer() {}

    private List<Observer> observers = new ArrayList<Observer>();
    private Context ctx;
    private SendBroadcastThread sendThread;
    private ReceiveBroadcastThread recThread;
    private DatagramSocket socket;
    private String ipAddress;

    public void setContext(Context ctx) {
        this.ctx = ctx;
    }
    public void addObserver(Observer ob) {
        synchronized (observers) {
            if(!observers.contains(ob)) observers.add(ob);
        }
    }

    public void removeObserver(Observer ob) {
        synchronized (observers) {
            observers.remove(ob);
        }
    }

    public interface Observer {
        void onDiscoveryFailed();
        void onDiscoveryStarted();
        void onDiscoveryService(String ipAddress, int commandPort, int jpgPort);
        void onDiscoveryFinished();
    }

    @Override
    public void onStart() {
        if(ctx == null) throw new IllegalStateException("Application context must be set before start!!!");
        onNotifyDiscoveryStarted();
        try {
            socket = new DatagramSocket();
            socket.setBroadcast(true);
        } catch (Exception e) {
            e.printStackTrace();
            Log.w(TAG, e.getMessage());
            onNotifyDiscoveryFailed();
            return;
        }
        ipAddress = Utils.getLocalIpAddress(ctx);
        // start receive broadcast thread
        recThread = new ReceiveBroadcastThread(this, socket);
        sendThread.start();

        // start send broadcast thread
        sendThread = new SendBroadcastThread(this, socket);
        sendThread.start();
    }

    @Override
    public void onStop() {
        release();
    }

    public void release() {
        try {
            if (socket != null) {
                socket.close();
                socket = null;
            }
            sendThread = null;
            recThread = null;
        } catch (Exception e) {
            e.printStackTrace();
            Log.w(TAG, e.getMessage());
        }
    }

    private void onNotifyDiscoveryFailed() {
        synchronized (this) {
            started = false;
            release();
        }

        synchronized (observers) {
           for(Observer ob : observers)
               ob.onDiscoveryFailed();
        }
    }
    private void onNotifyDiscoveryStarted() {
        synchronized (observers) {
            for(Observer ob : observers)
                ob.onDiscoveryStarted();
        }
    }
    private void onNotifyDiscoveryService(String ipAddress, int commandPort, int jpgPort) {
        synchronized (observers) {
            for(Observer ob : observers)
                ob.onDiscoveryService(ipAddress, commandPort, jpgPort);
        }
    }
    private void onNotifyDiscoveryFinished() {
        synchronized (this) {
            started = false;
            release();
        }
        synchronized (observers) {
            for(Observer ob : observers)
                ob.onDiscoveryFinished();
        }
    }
    private static class ReceiveBroadcastThread extends Thread {
        WeakReference<ServiceDiscoverer> dicoverer;
        DatagramSocket socket;
        String ipAddress;
        public ReceiveBroadcastThread(ServiceDiscoverer discoverer, DatagramSocket socket) {
            this.dicoverer = new WeakReference<ServiceDiscoverer>(discoverer);
            this.socket = socket;
            this.ipAddress = discoverer.ipAddress;
        }
        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            try {
                byte[] data = new byte[16];
                DatagramPacket packet = new DatagramPacket(data, data.length);
                socket.receive(packet);
                ServiceDiscoverResponse response = (ServiceDiscoverResponse)ResponseParser.parserResponse(ByteBuffer.wrap(data));
                if(response != null) {
                    if (dicoverer.get() != null) {
                        dicoverer.get().onNotifyDiscoveryService(response.ipAddress, response.commandPort, response.jpgPort);
                    }
                }
                if (dicoverer.get() != null) {
                    dicoverer.get().onNotifyDiscoveryFinished();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.w(TAG, e.getMessage());
                if (dicoverer.get() != null) {
                    dicoverer.get().onNotifyDiscoveryFailed();
                }
            }
        }
    }

    private static class SendBroadcastThread extends Thread {
        WeakReference<ServiceDiscoverer> dicoverer;
        DatagramSocket socket;
        String ipAddress;
        public SendBroadcastThread(ServiceDiscoverer discoverer, DatagramSocket socket) {
            this.dicoverer = new WeakReference<ServiceDiscoverer>(discoverer);
            this.socket = socket;
            this.ipAddress = discoverer.ipAddress;
        }

        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            try {
                socket.connect(InetAddress.getByName(Configure.BROADCAST_ADDRESS), Configure.BROADCAST_PORT);
                // send packet for 2 times;
                Request req = RequestFactory.createServiceDiscoverRequest(ipAddress, Configure.BROADCAST_REC_PORT);
                final DatagramPacket packet = new DatagramPacket(req.getDataPacket(),  req.getDataPacket().length);
                socket.send(packet);
                Thread.sleep(500);
                socket.send(packet);
            } catch (Exception e) {
                e.printStackTrace();
                Log.w(TAG, e.getMessage());
                if (dicoverer.get() != null) {
                    dicoverer.get().onNotifyDiscoveryFailed();
                }
            }
        }
    }
}