package com.webeye.photomaster.module;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by sean on 5/11/15.
 */
public class CommandCompressor extends ICompressor {
    private final static String TAG = "CommandCompressor";
    @Override
    public void init(Context ctx) {
        copyFile(ctx, "bin/djpeg", ctx.getApplicationInfo().dataDir + "/bin/djpeg");
        copyFile(ctx, "bin/cjpeg", ctx.getApplicationInfo().dataDir + "/bin/cjpeg");
    }

    @Override
    protected void compressJpgImpl(String source, String dest,
                                boolean openOptimizeOpt, int quality) {

    }

    private static void copyFile(Context context, String assetPath, String localPath) {
        try {
            File file = new File(localPath);
            if(!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } else {
                Log.i(TAG, localPath + "already exist!!!");
                return;
            }
            InputStream in = context.getAssets().open(assetPath);
            FileOutputStream out = new FileOutputStream(localPath);
            int read;
            byte[] buffer = new byte[4096];
            while ((read = in.read(buffer)) > 0) {
                out.write(buffer, 0, read);
            }
            out.close();
            in.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
