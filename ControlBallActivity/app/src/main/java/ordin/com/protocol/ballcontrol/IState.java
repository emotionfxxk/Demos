package ordin.com.protocol.ballcontrol;

/**
 * Created by sean on 4/17/15.
 */
public abstract class IState {

    protected boolean started = false;
    public synchronized void start() {
        if(started) return;
        started = true;
        onStart();
    }
    public synchronized void stop() {
        if(!started) return;
        started = false;
        onStop();
    }
    public abstract void onStart();
    public abstract void onStop();
}
