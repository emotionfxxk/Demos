package com.webeye.photomaster.jni;

/**
 * Created by sean on 5/7/15.
 */
public class ImageCompressor {
    static {
        System.loadLibrary("app");
    }
    public static native int compressJpeg(boolean openOptimize, int quality);
}
