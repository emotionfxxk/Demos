package ordin.com.protocol.connection;

import ordin.com.protocol.command.Command;

/**
 * Created by sean on 4/19/15.
 */
public interface IConnection {
    void sendCommand(Command cmd);
    void addCommandListener(CommandListener cmdListener);
    void removeCommandListener(CommandListener cmdListener);
}
