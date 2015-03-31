package mindarc.com.imagesender;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by sean on 15-2-28.
 */
public class DataSink extends State {
    final static String TAG = "DataSink";

    private ArrayList<ServerThread> mServerThreads = new ArrayList<ServerThread>();
    private ArrayList<Socket> mClientSockets = new ArrayList<Socket>();
    private LinkedBlockingDeque<Socket> mIldeSockets = new LinkedBlockingDeque<Socket>();
    private LinkedBlockingDeque<ImageUpdateMessage>  mUpdateMessageQueue = new LinkedBlockingDeque<ImageUpdateMessage>();

    final static int START_PORT = 55557;
    private int SERVER_COUNT = 6;
    private ThreadPoolExecutor mExecutor;

    private boolean mIsStarted;
    private AssetManager mAssetManager;
    public DataSink(AssetManager assetManager) {
        mAssetManager = assetManager;
        mIsStarted = false;
        // init thread pool executor
        mExecutor = new ThreadPoolExecutor(SERVER_COUNT, SERVER_COUNT, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2));
    }

    @Override
    public void onStarted() {
        startListen();
    }
    @Override
    public void onStopped() {
        stopListen();
        stopClient();
        mUpdateMessageQueue.clear();
    }

    public void sendUpdateMessage(ImageUpdateMessage msg) {
        if(!mIsStarted) {
            Log.i(TAG, "not started, drop update message!");
            return;
        }
        mUpdateMessageQueue.offer(msg);
    }

    private void startListen() {
        synchronized(mServerThreads) {
            for(ServerThread thread : mServerThreads) {
                if(thread != null) thread.stopServerThread();
            }
            mServerThreads.clear();
            for(int index = 0; index < SERVER_COUNT; ++index) {
                ServerThread server = new ServerThread(this, START_PORT + index);
                mServerThreads.add(server);
                server.start();
            }
        }

    }
    private void stopListen() {
        synchronized(mServerThreads) {
            for (ServerThread thread : mServerThreads) {
                if (thread != null) thread.stopServerThread();
            }
            mServerThreads.clear();
        }
    }

    private void stopClient() {
        synchronized (mIldeSockets) {
            mIldeSockets.clear();
        }
        synchronized (mClientSockets) {
            for(Socket socket: mClientSockets) {
                if(socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            mClientSockets.clear();
        }
    }

    private void onAcceptClient(Socket clientSocket) {
        synchronized (mClientSockets) {
            mClientSockets.add(clientSocket);
        }
        synchronized (mIldeSockets) {
            mIldeSockets.offer(clientSocket);
        }
        scheduleUpdateTask();
    }

    private SendImageTask.OnSendImageTaskCallback mSendImageCallback = new SendImageTask.OnSendImageTaskCallback() {
        @Override
        public void onSendImageFailed(Socket client) {

        }
        @Override
        public void onSendImageSucceed(Socket client) {
            // put client into IDLE list
            synchronized (mIldeSockets) {
                mIldeSockets.offer(client);
            }
            scheduleUpdateTask();

        }
    };

    private void scheduleUpdateTask() {
        if(!mIsStarted) {
            Log.i(TAG, "not started, failed to schedule update task!");
            return;
        }
        Socket client = null;
        ImageUpdateMessage message = null;
        synchronized (mUpdateMessageQueue) {
            while(!mUpdateMessageQueue.isEmpty()) {
                client = null;
                synchronized (mIldeSockets) {
                    if(!mIldeSockets.isEmpty()) {
                        client = mIldeSockets.poll();
                    }
                }
                if(client == null) {
                    Log.i(TAG, "no idle client socket, give up scheduling!");
                    return;
                }
                message = mUpdateMessageQueue.poll();
                mExecutor.execute(new SendImageTask(client, mAssetManager, message.filePath, message.filePos, mSendImageCallback));
            }
        }
    }

    private static class ServerThread extends Thread {
        private int mPort;
        private WeakReference<DataSink> mService;
        private ServerSocket mServerSocket;
        public ServerThread(DataSink service, int port) {
            mPort = port;
            mService = new WeakReference<DataSink>(service);
        }

        public void stopServerThread() {
            if(mServerSocket != null) {
                try {
                    mServerSocket.close();
                    mServerSocket = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        @Override
        public void run() {
            Log.i(TAG, "ServerThread start to running ......");
            try {
                mServerSocket = new ServerSocket(mPort);
                while((mService.get() != null) && mService.get().isStarted()) {
                    Log.i(TAG, "ServerThread before accept ......");
                    Socket clientSocket =  mServerSocket.accept();
                    Log.i(TAG, "ServerThread start to running ......");
                    if(mService.get() != null) {
                        mService.get().onAcceptClient(clientSocket);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.i(TAG, "ServerThread arise exception......, stop Image service");
                if(mService.get() != null) {
                    mService.get().stop();
                }
            }
            Log.i(TAG, "ServerThread exit running ......");
        }
    }
}
