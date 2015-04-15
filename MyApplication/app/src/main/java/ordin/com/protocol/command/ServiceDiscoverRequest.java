package ordin.com.protocol.command;

import android.util.Log;

import java.nio.ByteBuffer;

/**
 * Created by sean on 4/8/15.
 */
public class ServiceDiscoverRequest extends Request {
    protected final static byte type = 1;
    protected final static byte netCardCount = 1;
    protected final int ipAddress;
    protected final short port;
    public ServiceDiscoverRequest(String ipAddress, int port) {
        if(ipAddress == null) {
            throw new IllegalArgumentException("ip address be null");
        }
        String[] values = ipAddress.split("\\.");
        // only support IP V4 now
        if(values.length != 4) {
            throw new IllegalArgumentException("ip address mal format error:" + ipAddress);
        }
        Log.i("Test", "first:" + Integer.valueOf(values[0]).byteValue());
        this.ipAddress = Integer.valueOf(values[0]) << 24 | Integer.valueOf(values[1]) << 16 |
                Integer.valueOf(values[2]) << 8 | Integer.valueOf(values[3]);
        if(port < 0 || port > 65535) {
            throw new IllegalArgumentException("port should be range in 0 ~ 65536");
        }
        this.port = (short) port;
        this.command = CommandDefs.CMD_DISCOVER_SERVICE;
    }
    @Override
    public short getPayloadLength() {
        return 8;
    }
    @Override
    public void fillPayload(ByteBuffer byteBuffer) {
        byteBuffer.put(type);
        byteBuffer.put(netCardCount);
        byteBuffer.putInt(ipAddress);
        byteBuffer.putShort(port);
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{cmd=").append(command).append(", ipAddress=").append((ipAddress >> 24) & 0xFF).append(".")
                .append((ipAddress >> 16) & 0xFF).append(".").append((ipAddress >> 8) & 0xFF).append(".")
                .append(ipAddress & 0xFF).append(", port=").append(port);
        return sb.toString();
    }
}
