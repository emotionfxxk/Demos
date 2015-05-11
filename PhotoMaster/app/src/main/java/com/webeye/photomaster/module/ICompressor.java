package com.webeye.photomaster.module;

import android.content.Context;
import android.util.Log;

/**
 * Created by sean on 5/11/15.
 */
public abstract class ICompressor {
    public final static String TAG = "ICompressor";
    public interface CompressBallback {
        void onCompressStarted(String source, String dest);
        void onCompressFinished(String source, String dest);
    }
    public abstract void init(Context ctx);


    public void compressJpg(String source, String dest,
                            boolean openOptimizeOpt, int quality, CompressBallback cb) {
        new CompressThread(this, source, dest, openOptimizeOpt, quality, cb).start();
    }

    protected abstract void compressJpgImpl(String source, String dest,
                                boolean openOptimizeOpt, int quality);

    private static class CompressThread extends Thread {
        String source, dest;
        boolean openOptimzie;
        int quality;
        CompressBallback cb;
        ICompressor compressor;
        public CompressThread(ICompressor compressor, String source, String dest,
                              boolean openOptimizeOpt, int quality, CompressBallback cb) {
            this.source = source;
            this.dest = dest;
            this.openOptimzie = openOptimizeOpt;
            this.quality = quality;
            this.cb = cb;
            this.compressor = compressor;
        }
        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            Log.i(TAG, "start compress image:" + source);
            if(cb != null) cb.onCompressStarted(source, dest);
            compressor.compressJpgImpl(source, dest, openOptimzie, quality);
            if(cb != null) cb.onCompressFinished(source, dest);
            Log.i(TAG, "finished compress image:" + source);
        }
    }
}
