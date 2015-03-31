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

/**
 * Created by sean on 15-2-28.
 */
public class ImageSenderService {
    final static String TAG = "ImageSenderService";
    final static int PORT = 55557;
    private boolean mIsStarted;
    private ServerThread mServerThread;
    private ClientThread mClientThread;
    private AssetManager mAssetManager;
    public ImageSenderService(AssetManager assetManager) {
        mAssetManager = assetManager;
        mIsStarted = false;
    }
    public synchronized void start() {
        if(!mIsStarted) {
            mIsStarted = true;
            startListen();
        }
    }
    public synchronized void stop() {
        if(mIsStarted) {
            mIsStarted = false;
            stopListen();
            stopClient();
        }
    }
    public synchronized boolean isServiceStarted() {
        return mIsStarted;
    }
    private void startListen() {
        if(mServerThread != null) mServerThread.stopServerThread();
        mServerThread = new ServerThread(this);
        mServerThread.start();
    }
    private void stopListen() {
        if(mServerThread != null) {
            mServerThread.stopServerThread();
            mServerThread = null;
        }
    }

    private void stopClient() {
        if(mClientThread != null) {
            mClientThread.stopClientSocket();
            mClientThread = null;
        }
    }

    private void onAcceptClient(Socket clientSocket) {
        if(mClientThread != null) mClientThread.stopClientSocket();
        mClientThread = new ClientThread(this, clientSocket);
        mClientThread.start();
    }

    private static class ServerThread extends Thread {
        private WeakReference<ImageSenderService> mService;
        private ServerSocket mServerSocket;
        public ServerThread(ImageSenderService service) {
            mService = new WeakReference<ImageSenderService>(service);
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
                mServerSocket = new ServerSocket(PORT);
                while((mService.get() != null) && mService.get().isServiceStarted()) {
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

    private static class ClientThread extends Thread {
        private final static int PIC_COUNT = 11;
        private WeakReference<ImageSenderService> mService;
        private Socket mClientSocket;
        private int mFileIndex = 0;
        private int mFileCount = 0;
        public ClientThread(ImageSenderService service, Socket clientSocket) {
            mService = new WeakReference<ImageSenderService>(service);
            mClientSocket = clientSocket;
        }
        public void stopClientSocket() {
            if(mClientSocket != null) {
                try {
                    mClientSocket.close();
                    mClientSocket = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        @Override
        public void run() {
            Log.i(TAG, "ClientThread start to running ......");
            OutputStream os = null;
            mFileCount = 0;
            try {
                os = mClientSocket.getOutputStream();
                byte[] buffer = new byte[8 * 1024];
                long startTime = System.currentTimeMillis();
                while((mService.get() != null) && mService.get().isServiceStarted()) {
                    Log.i(TAG, "ClientThread start sending file:" + mFileIndex + ".jpg" + ", fileCount:" + mFileCount);

                    AssetFileDescriptor afd = mService.get().mAssetManager.openFd(String.valueOf(mFileIndex++ % PIC_COUNT) + ".jpg");
                    int fileLength = (int)afd.getLength();
                    int remainLength = fileLength;
                    // send file length(8 bytes) and file count(4 bytes) here
                    ByteBuffer fileCountBuffer = ByteBuffer.allocate(4);
                    fileCountBuffer.putInt(mFileCount);
                    os.write(fileCountBuffer.array(), 0, 4);
                    ByteBuffer fileLengthBuffer = ByteBuffer.allocate(4);
                    fileLengthBuffer.putInt(fileLength);
                    os.write(fileLengthBuffer.array(), 0, 4);

                    int readLength;
                    Log.i(TAG, "ClientThread file length:" + fileLength);
                    BufferedInputStream bis = new BufferedInputStream(afd.createInputStream());
                    while(remainLength > 0) {
                        readLength = bis.read(buffer, 0, buffer.length);
                        remainLength -= readLength;
                        os.write(buffer, 0, readLength);
                        os.flush();
                    }
                    afd.close();
                    ++mFileCount;
                    Log.i(TAG, "average cost: " + (System.currentTimeMillis() - startTime)/mFileCount + "ms");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (os != null) {
                        os.close();
                    }
                    stopClientSocket();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.i(TAG, "ClientThread exit running ......");
        }
    }
}
