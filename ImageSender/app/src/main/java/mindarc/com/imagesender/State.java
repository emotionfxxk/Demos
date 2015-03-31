package mindarc.com.imagesender;

/**
 * Created by sean on 3/30/15.
 */
public abstract class State {
    protected boolean mIsStarted = false;
    public synchronized final void start() {
        if(!mIsStarted) {
            mIsStarted = true;
            onStarted();
        }
    }
    public synchronized final void stop() {
        if(mIsStarted) {
            mIsStarted = false;
            onStopped();
        }
    }
    public synchronized final boolean isStarted() {
        return mIsStarted;
    }
    public abstract void onStarted();
    public abstract void onStopped();
}
