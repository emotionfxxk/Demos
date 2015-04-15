package mindarc.com.imageclient;

import mindarc.com.imageclient.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class FullscreenActivity extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;
    //private ImageView mImage;
    private ImageReceiver mImageReceiver;
    private GridView mPlanBoard;
    private VideoApdater mPlanBoardAdapter;
    private int[] mStatics = new int[18];
    private long[] mStartTimes = new long[18];
    private final static String TAG = "FullscreenActivity";
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ImageReceiver.MSG_ON_REC_IMG:
                    //mImage.setImageBitmap((Bitmap)msg.obj);
                    if(mStatics[msg.arg1] == 0) {
                        mStartTimes[msg.arg1] = System.currentTimeMillis();
                    }
                    mStatics[msg.arg1]++;
                    if((System.currentTimeMillis() - mStartTimes[msg.arg1]) > 0) {
                        Log.i(TAG, "pos:" + msg.arg1 + ", fps:" + mStatics[msg.arg1] / ((System.currentTimeMillis() - mStartTimes[msg.arg1]) / 1000f));
                    }
                    mPlanBoardAdapter.updateVideoImage(msg.arg1, (Bitmap)msg.obj);
                    break;
            }
        }
    };

    private static class VideoApdater extends BaseAdapter {
        private SparseArray<ImageView> mImageViews;
        public VideoApdater(int videoCount) {
            mImageViews = new SparseArray<ImageView>();
            for(int pos = 0; pos < videoCount; ++pos) {
                mImageViews.put(pos, null);
            }
        }
        public void updateVideoImage(int position, Bitmap bitmap) {
            ImageView video = null;
            synchronized (mImageViews) {
                video = (ImageView) mImageViews.get(position, null);
            }
            if(video != null) {
                video.setImageBitmap(bitmap);
            }
        }
        @Override
        public int getCount() {
            int count = 0;
            synchronized (mImageViews) {
                count = mImageViews.size();
            }
            return count;
        }

        @Override
        public Object getItem(int position) {
            Object obj = null;
            synchronized (mImageViews) {
                obj = mImageViews.get(position);
            }
            return obj;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView video = null;
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                video = (ImageView)inflater.inflate(R.layout.video_item, parent, false);
            } else {
                video = (ImageView)convertView;
            }
            synchronized (mImageViews) {
                mImageViews.put(position, video);
            }
            //video.setImageResource(R.drawable.default);
            return video;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);
        //mImage = (ImageView) findViewById(R.id.fullscreen_content);
        mPlanBoard = (GridView) findViewById(R.id.plan_board);
        mPlanBoardAdapter = new VideoApdater(18);
        mPlanBoard.setAdapter(mPlanBoardAdapter);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.plan_board);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        /*
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });*/

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
        mImageReceiver = new ImageReceiver(mHandler);
    }

    @Override
    public void onResume() {
        super.onResume();
        mImageReceiver.start();
    }
    @Override
    public void onPause() {
        super.onPause();
        mImageReceiver.stop();
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
