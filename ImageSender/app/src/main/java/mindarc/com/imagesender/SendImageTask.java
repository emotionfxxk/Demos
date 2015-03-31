package mindarc.com.imagesender;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * Created by sean on 3/30/15.
 */
public class SendImageTask implements Runnable {
    private final static String TAG = "SendImageTask";
    public interface OnSendImageTaskCallback {
        void onSendImageFailed(Socket client);
        void onSendImageSucceed(Socket client);
    }
    private Socket mClient;
    private OnSendImageTaskCallback mCallback;
    private AssetManager mAssetManager;
    private String mFileName;
    private int mFilePos;
    public SendImageTask(Socket client, AssetManager assetManager, String fileName, int filePos, OnSendImageTaskCallback cb) {
        if(client == null || assetManager == null || fileName == null || cb == null)
            throw new IllegalArgumentException("Client, asset manager, file name or callback should not be null");
        mClient = client;
        mAssetManager = assetManager;
        mFileName = fileName;
        mFilePos = filePos;
        mCallback = cb;
    }
    @Override
    public void run() {
        Log.i(TAG, "start transfer image:" + mFileName + ", for pos:" + mFilePos);
        OutputStream os = null;
        AssetFileDescriptor afd = null;
        try {
            long startTime = System.currentTimeMillis();
            os = mClient.getOutputStream();
            byte[] buffer = new byte[8 * 1024];
            afd = mAssetManager.openFd(mFileName);
            int fileLength = (int)afd.getLength();
            int remainLength = fileLength;
            // send file pos(4 bytes) and file count(4 bytes) here
            ByteBuffer fileCountBuffer = ByteBuffer.allocate(4);
            fileCountBuffer.putInt(mFilePos);
            os.write(fileCountBuffer.array(), 0, 4);

            ByteBuffer fileLengthBuffer = ByteBuffer.allocate(4);
            fileLengthBuffer.putInt(fileLength);
            os.write(fileLengthBuffer.array(), 0, 4);

            int readLength;
            BufferedInputStream bis = new BufferedInputStream(afd.createInputStream());
            while(remainLength > 0) {
                readLength = bis.read(buffer, 0, buffer.length);
                remainLength -= readLength;
                os.write(buffer, 0, readLength);
                os.flush();
            }
            Log.i(TAG, "cost: " + (System.currentTimeMillis() - startTime) + "ms");
            mCallback.onSendImageSucceed(mClient);
        } catch (Exception e) {
            Log.e(TAG, "exception raised while transferring file:" + e.getMessage());
            mCallback.onSendImageFailed(mClient);
        } finally {
            try {
                if(afd != null) {
                    afd.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}