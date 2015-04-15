package ordin.com.protocol.command;

/**
 * Create command instance by params
 * Created by sean on 4/8/15.
 */
public class RequestFactory {

    public static Request createServiceDiscoverRequest(String ipAddress, int port) {
        return new ServiceDiscoverRequest(ipAddress, port);
    }
}
