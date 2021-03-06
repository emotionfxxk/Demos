package mindarc.com.imageclient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by sean on 15-3-12.
 */
public class ImageReceiver {
    public final static int MSG_ON_REC_IMG = 0;
    private final static String TAG = "ImageReceiver";
    private final static int CONN_TIMEOUT = 5000;
    private final static int DEFAULT_BUFFER_SIZE = 64 * 1024;
    private final static String HOST = "192.168.100.20";
    private final static int START_PORT = 55557;
    private final static int SERVER_COUNT = 5;
    private ArrayList<ReceiveImageThread> mServerThreads = new ArrayList<ReceiveImageThread>();

    private boolean mStarted = false;
    private int mBitmapIndex = 0;
    private final Handler mHandler;
    //private ReceiveImageThread mThread;
    public ImageReceiver(Handler handler) {
        mHandler = handler;
    }
    public synchronized void start() {
        if(!mStarted) {
            for(int index = 0; index < SERVER_COUNT; ++index) {
                ReceiveImageThread thread = new ReceiveImageThread(START_PORT + index);
                thread.start();
                mServerThreads.add(thread);
            }
            mStarted = true;
        }
    }
    public synchronized void stop() {
        if(mStarted) {
            for(ReceiveImageThread thread: mServerThreads) {
                thread.stopRunning();
            }
            mServerThreads.clear();
            mStarted = false;
        }
    }
    private class ReceiveImageThread extends Thread {
        private Bitmap[] mInBitmapCache;
        int mPort;
        InputStream mIs;
        Socket mSocket;
        boolean mRunning;
        private byte[] mBuffer = new byte[DEFAULT_BUFFER_SIZE];
        public ReceiveImageThread(int port) {
            mPort = port;
        }
        public void stopRunning() {
            synchronized (this) {
                mRunning = false;
            }
            release();
        }
        @Override
        public void start() {
            mRunning = true;
            super.start();
        }
        @Override
        public void run() {
            mInBitmapCache = new Bitmap[2];
            mInBitmapCache[0] = Bitmap.createBitmap(240, 160, Bitmap.Config.RGB_565);
            mInBitmapCache[1] = Bitmap.createBitmap(240, 160, Bitmap.Config.RGB_565);
            //Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
            // TODO: do receive work here
            mSocket = new Socket();
            InetSocketAddress isa = new InetSocketAddress(HOST, mPort);
            try {
                Log.i(TAG, "before connect to server ...");
                mSocket.connect(isa, CONN_TIMEOUT);
                Log.i(TAG, "connected");
                mIs = mSocket.getInputStream();
                Long startTime = System.currentTimeMillis();
                int count = 0;
                while (mRunning) {
                    readImageData();
                    ++count;
                    float cost = (System.currentTimeMillis() - startTime) / 1000f;
                    Log.i(TAG, "fps:" + count / cost);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Render thread exit due to:" + e.getMessage());
            } finally {
                release();
            }
        }
        protected void readImageData() throws IOException {
            // read picture count and file length first

            int picPos;
            int fileLength;
            int lengthOfPicCount = mIs.read(mBuffer, 0, 4);
            if(lengthOfPicCount != 4) {
                throw new IOException("Mal format: failed to read pic count");
            } else {
                ByteBuffer bb = ByteBuffer.wrap(mBuffer);
                bb.order(ByteOrder.BIG_ENDIAN);
                picPos = bb.getInt();
            }

            int lengthOfFileLength = mIs.read(mBuffer, 0, 4);
            if(lengthOfFileLength != 4) {
                throw new IOException("Mal format: failed to read file length");
            } else {
                ByteBuffer bb = ByteBuffer.wrap(mBuffer);
                bb.order(ByteOrder.BIG_ENDIAN);
                fileLength = bb.getInt();
            }
            Log.i(TAG, "picPos:" + picPos + ", fileLength:" + fileLength);
            if(fileLength > DEFAULT_BUFFER_SIZE) {
                mBuffer = new byte[fileLength];
            }

            int readFileLength = 0, readLength = 0;
            while(readFileLength < fileLength) {
                readLength = mIs.read(mBuffer, readFileLength, fileLength - readFileLength);
                if(readLength == -1) {
                    throw new IOException("END OF STREAM");
                }
                readFileLength += readLength;
            }

            long start = System.currentTimeMillis();
            if(readFileLength != fileLength) {
                throw new IOException("Mal file length, readFileLength:" + readFileLength);
            } else {
                BitmapFactory.Options op = new BitmapFactory.Options();
                op.inPreferredConfig = Bitmap.Config.RGB_565;
                op.inBitmap = mInBitmapCache[mBitmapIndex];
                mBitmapIndex = (mBitmapIndex + 1) % 2;
                //Bitmap bitmap = BitmapFactory.decodeByteArray(mBuffer, 0, fileLength, op);
                Message msg = new Message();
                msg.what = MSG_ON_REC_IMG;
                msg.arg1 = picPos;
                msg.obj = null;
                if(mHandler != null) mHandler.sendMessage(msg);
            }
            long stop = System.currentTimeMillis();
            Log.i(TAG, "decode cost:" + (stop - start));

        }
        protected void release() {
            if(mSocket != null) {
                try {
                    mSocket.close();
                    mSocket = null;
                    if(mIs != null) {
                        mIs.close();
                        mIs = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mHandler.removeMessages(MSG_ON_REC_IMG);
            for(Bitmap bitmap : mInBitmapCache) {
                if(bitmap != null) {
                    bitmap.recycle();
                }
            }
        }
    }
}
