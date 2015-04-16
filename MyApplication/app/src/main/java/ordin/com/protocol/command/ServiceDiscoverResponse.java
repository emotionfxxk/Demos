package ordin.com.protocol.command;

import java.nio.ByteBuffer;

/**
 * Created by sean on 4/14/15.
 */
public class ServiceDiscoverResponse extends Response {
    public String ipAddress;
    public int commandPort;
    public int jpgPort;

    public static Creator sCreator = new Creator() {
        @Override
        public Response createInstance() {
            return new ServiceDiscoverResponse();
        }
    };

    private ServiceDiscoverResponse() {}

    @Override
    public void parsePayload(ByteBuffer byteBuffer) {
        // get ip address
        byte[] ipAddressBytes = new byte[4];
        byteBuffer.get(ipAddressBytes);
        StringBuilder ipStringBuilder = new StringBuilder();
        ipStringBuilder.append(ipAddressBytes[0] & 0x000000FF).append(".")
                .append(ipAddressBytes[1] & 0x000000FF).append(".")
                .append(ipAddressBytes[2] & 0x000000FF).append(".")
                .append(ipAddressBytes[3] & 0x000000FF);
        ipAddress = ipStringBuilder.toString();

        // get command port
        commandPort = byteBuffer.getShort();

        // get jpg port
        jpgPort = byteBuffer.getShort();
    }
}
