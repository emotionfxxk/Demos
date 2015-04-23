package ordin.com.protocol.connection;

/**
 * Created by sean on 4/23/15.
 */
public interface ConnectionStateListener {
    void onConnecting(IConnection con);
    void onConnected(IConnection con);
    void onDisconnected(IConnection con);
}
