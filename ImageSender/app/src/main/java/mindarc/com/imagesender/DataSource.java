package mindarc.com.imagesender;

import android.os.Handler;

/**
 * Created by sean on 3/30/15.
 */
public class DataSource extends State {
    private DataSink mSink;
    // update image every 33 mm
    private int INTERVAL = 33;
    private int mImageIndex = 0;
    private Handler mHanlder = new Handler();
    private Runnable mUpdateTask = new Runnable() {
        @Override
        public void run() {
            for(int imagePos = 0; imagePos < 18; ++imagePos) {
                mSink.sendUpdateMessage(new ImageUpdateMessage(String.valueOf(mImageIndex) + ".jpg", imagePos));
            }
            mImageIndex = (mImageIndex + 1) % 10;
            if(isStarted()) mHanlder.postDelayed(mUpdateTask, INTERVAL);
        }
    };

    public void setDataSink(DataSink sink) {
        mSink = sink;
    }
    @Override
    public void onStarted() {
        if(mSink == null) throw new IllegalStateException("Data sink must be set before start!");
        mSink.start();
        mHanlder.postDelayed(mUpdateTask, INTERVAL);
    }

    @Override
    public void onStopped() {
        if(mSink == null) throw new IllegalStateException("Data sink must be set before stop!");
        mSink.stop();
        mHanlder.removeCallbacks(mUpdateTask);
    }
}
