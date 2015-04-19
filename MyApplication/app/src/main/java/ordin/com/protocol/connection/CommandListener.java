package ordin.com.protocol.connection;

import ordin.com.protocol.command.Command;

/**
 * Created by sean on 4/19/15.
 */
public interface CommandListener {
    void onSentCommand(Command cmd);
    void onReceivedCommand(Command cmd);
}
