package com.webeye.photomaster;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.webeye.photomaster.jni.ImageCompressor;
import com.webeye.photomaster.module.JpgOptimCmdCompressor;
import com.webeye.photomaster.module.JpgTurboCmdCompressor;
import com.webeye.photomaster.module.ICompressor;


public class PhotoMasterDemo extends ActionBarActivity implements ICompressor.CompressBallback {
    private final static String TAG = "PhotoMasterDemo";
    private ICompressor compressor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_master_demo);
        Log.i(TAG, "compress return:" + ImageCompressor.compressJpeg(true, 50));

        /*
        compressor = new JpgTurboCmdCompressor();
        compressor.init(this);
        compressor.compressJpg("/sdcard/test_orig.jpg", "/sdcard/test_optim_60.jpg", true, 60, this);*/

        compressor = new JpgOptimCmdCompressor();
        compressor.init(this);
        compressor.compressJpg("/sdcard/test_orig.jpg", "/sdcard/test_jpegoptim_60.jpg", true, 60, this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photo_master_demo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCompressStarted(String source, String dest) {
        Log.i(TAG, "onCompressStarted:" + source);
    }
    @Override
    public void onCompressFinished(String source, String dest) {
        Log.i(TAG, "onCompressFinished:" + source);
    }
}
