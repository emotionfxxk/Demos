package ordin.com.protocol.connection;

import java.util.ArrayList;


/**
 * Created by sean on 4/19/15.
 */
public abstract class BaseConnection extends IState implements IConnection {
    protected ArrayList<CommandListener> cmdListeners = new ArrayList<CommandListener>();
    protected ArrayList<ConnectionStateListener> stateListeners = new ArrayList<ConnectionStateListener>();

    @Override
    public void addCommandListener(CommandListener cmdListener) {
        synchronized (cmdListeners) {
            if(!cmdListeners.contains(cmdListener)) {
                cmdListeners.add(cmdListener);
            }
        }
    }

    @Override
    public void removeCommandListener(CommandListener cmdListener) {
        synchronized (cmdListeners) {
            cmdListeners.remove(cmdListener);
        }
    }

    @Override
    public void addStateListener(ConnectionStateListener stateListener) {
        synchronized (stateListeners) {
            if(!stateListeners.contains(stateListener)) {
                stateListeners.add(stateListener);
            }
        }
    }

    @Override
    public void removeStateListener(ConnectionStateListener stateListener) {
        synchronized (stateListeners) {
            stateListeners.remove(stateListener);
        }
    }
}
