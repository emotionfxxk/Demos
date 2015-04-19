package ordin.com.protocol.connection;

import java.util.ArrayList;


/**
 * Created by sean on 4/19/15.
 */
public abstract class BaseConnection extends IState implements IConnection {
    protected ArrayList<CommandListener> listeners = new ArrayList<CommandListener>();


    @Override
    public void addCommandListener(CommandListener cmdListener) {
        synchronized (listeners) {
            if(!listeners.contains(cmdListener)) {
                listeners.add(cmdListener);
            }
        }
    }

    @Override
    public void removeCommandListener(CommandListener cmdListener) {
        synchronized (listeners) {
            listeners.remove(cmdListener);
        }
    }
}
