package ordin.com.protocol.ballcontrol;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by sean on 5/3/15.
 */
public class BallActionFactory extends IState implements SensorEventListener {
    private final static String TAG = "BallActionFactory";
    private SensorManager sensorManager;
    private float absGxThreshold, absGyThreshold;
    private int repeatInterval;
    private int countExceedThresholdX, countExceedThresholdY;
    private byte lastActionX, lastActionY;
    private ActionListener actionListener;

    public interface ActionListener {
        void onAction(BallControlAction action);
    }

    public BallActionFactory(Context ctx) {
        sensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
        // default threshold as 4.0
        absGxThreshold = absGyThreshold = 4.0f;
        repeatInterval = 3;
    }

    public void setThreshHold(float gxShreshold, float gyShreshold) {
        absGxThreshold = gxShreshold;
        absGyThreshold = gyShreshold;
    }

    public void setRepeatInterval(int repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    public void setActionListener(ActionListener listener) {
        actionListener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                Log.i(TAG, "ACC : values length:" + event.values.length + ", g x=" + event.values[0]
                        + ", g y=" + event.values[1] + ", g z=" + event.values[2]);
                onProcessAccelerateData(event.values[0], event.values[1], event.values[2]);
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onStart() {
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        lastActionX = lastActionY = BallControlAction.NONE;
        countExceedThresholdX = countExceedThresholdY = 0;
    }

    @Override
    public void onStop() {
        sensorManager.unregisterListener(this);
    }

    protected void onProcessAccelerateData(float gx, float gy, float gz) {
        if(Math.abs(gy) >= absGyThreshold) {
            if(gy > 0) {
                // down
                if(lastActionY != BallControlAction.MOVE_DOWN) {
                    lastActionY = BallControlAction.MOVE_DOWN;
                    countExceedThresholdY = 0;
                }
                if((countExceedThresholdY % repeatInterval) == 0) {
                    onGenerateBallControlAction(BallControlAction.MOVE_DOWN);
                }
                countExceedThresholdY = (++countExceedThresholdY) % repeatInterval;
            } else {
                // up
                if(lastActionY != BallControlAction.MOVE_UP) {
                    lastActionY = BallControlAction.MOVE_UP;
                    countExceedThresholdY = 0;
                }
                if((countExceedThresholdY % repeatInterval) == 0) {
                    onGenerateBallControlAction(BallControlAction.MOVE_UP);
                }
                countExceedThresholdY = (++countExceedThresholdY) % repeatInterval;
            }
        } else {
            lastActionY = BallControlAction.NONE;
            countExceedThresholdY = 0;
        }

        if(Math.abs(gx) >= absGxThreshold) {
            if(gx < 0) {
                // right
                if(lastActionX != BallControlAction.MOVE_RIGHT) {
                    lastActionX = BallControlAction.MOVE_RIGHT;
                    countExceedThresholdX = 0;
                }
                if((countExceedThresholdX % repeatInterval) == 0) {
                    onGenerateBallControlAction(BallControlAction.MOVE_RIGHT);
                }
                countExceedThresholdX = (++countExceedThresholdX) % repeatInterval;
            } else {
                // left
                if(lastActionX != BallControlAction.MOVE_LEFT) {
                    lastActionX = BallControlAction.MOVE_LEFT;
                    countExceedThresholdX = 0;
                }
                if((countExceedThresholdX % repeatInterval) == 0) {
                    onGenerateBallControlAction(BallControlAction.MOVE_LEFT);
                }
                countExceedThresholdX = (++countExceedThresholdX) % repeatInterval;
            }
        } else {
            lastActionX = BallControlAction.NONE;
            countExceedThresholdX = 0;
        }
    }

    protected void onGenerateBallControlAction(byte event) {
        if(actionListener != null) {
            actionListener.onAction(new BallControlAction(event));
        }
    }
}
