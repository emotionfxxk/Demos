package ordin.com.protocol.command;

import java.nio.ByteBuffer;

/**
 * Created by sean on 4/15/15.
 */
public class CommandOnlyRequest extends Request {
    public CommandOnlyRequest(byte command) {
        this.command = command;
    }
    @Override
    public short getPayloadLength() {
        return 0;
    }
    @Override
    public void fillPayload(ByteBuffer byteBuffer) {
        // Do nothing here
    }
}
