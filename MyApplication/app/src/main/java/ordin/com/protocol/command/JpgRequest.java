package ordin.com.protocol.command;


import java.nio.ByteBuffer;

/**
 * Created by sean on 4/16/15.
 */
public class JpgRequest extends Request {
    public byte imageType;
    public byte openSubsequentImg;
    public byte resolutionX;
    public byte resolutionY;

    public JpgRequest(String ipAddress, int port) {
        throw new UnsupportedOperationException("Not supported");
    }
    @Override
    public short getPayloadLength() {
        return 8;
    }
    @Override
    public void fillPayload(ByteBuffer byteBuffer) {
        throw new UnsupportedOperationException("Not supported");
    }
}
