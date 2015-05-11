package com.webeye.photomaster.module;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by sean on 5/11/15.
 */
public class JpgTurboCmdCompressor extends ICompressor {
    private final static String TAG = "JpgTurboCmdCompressor";
    private Context ctx;
    @Override
    public void init(Context ctx) {
        this.ctx = ctx;
        copyFile(ctx, "bin/djpeg", ctx.getApplicationInfo().dataDir + "/bin/djpeg");
        copyFile(ctx, "bin/cjpeg", ctx.getApplicationInfo().dataDir + "/bin/cjpeg");
    }

    @Override
    protected void compressJpgImpl(String source, String dest,
                                boolean openOptimizeOpt, int quality) {
        try {
            Process chmodProcess = Runtime.getRuntime().exec("chmod 777 " + ctx.getApplicationInfo().dataDir + "/bin/djpeg");
            chmodProcess.waitFor();

            chmodProcess = Runtime.getRuntime().exec("chmod 777 " + ctx.getApplicationInfo().dataDir + "/bin/cjpeg");
            chmodProcess.waitFor();

            {
                StringBuilder djpegCmd = new StringBuilder();
                djpegCmd.append(ctx.getApplicationInfo().dataDir).append("/bin/djpeg -targa -outfile ")
                        .append(source).append(".targa ").append(source);
                Process djpegProcess = Runtime.getRuntime().exec(djpegCmd.toString());
                BufferedReader reader = new BufferedReader(new InputStreamReader(djpegProcess.getInputStream()));
                int read;
                char[] buffer = new char[4096];
                StringBuffer output = new StringBuffer();
                while ((read = reader.read(buffer)) > 0) {
                    output.append(buffer, 0, read);
                }
                reader.close();
                djpegProcess.waitFor();
                Log.e(TAG, "djpeg output: " + output.toString());
            }

            {
                StringBuilder cjpegCmd = new StringBuilder();
                cjpegCmd.append(ctx.getApplicationInfo().dataDir).append("/bin/cjpeg ");
                if(openOptimizeOpt) cjpegCmd.append("-optimize ");
                cjpegCmd.append(" -quality ").append(quality).append(" -outfile ").append(dest).append(" ")
                        .append(source).append(".targa");
                Log.i(TAG, "cjpegCmd command: " + cjpegCmd.toString());
                Process cjpegProcess = Runtime.getRuntime().exec(cjpegCmd.toString());
                BufferedReader reader = new BufferedReader(new InputStreamReader(cjpegProcess.getInputStream()));
                int read;
                char[] buffer = new char[4096];
                StringBuffer output = new StringBuffer();
                while ((read = reader.read(buffer)) > 0) {
                    output.append(buffer, 0, read);
                }
                reader.close();
                cjpegProcess.waitFor();
                Log.e(TAG, "cjpeg output: " + output.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "exception: " + e.getMessage());
        }

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
