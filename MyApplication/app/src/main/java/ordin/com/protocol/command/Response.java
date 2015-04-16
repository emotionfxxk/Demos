package ordin.com.protocol.command;

import java.nio.ByteBuffer;

/**
 * Created by sean on 4/8/15.
 */
public abstract class Response extends Command {
    public static interface Creator {
        Response createInstance();
    };

    public Creator sCreator;

    @Override
    public short getPayloadLength() {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void fillPayload(ByteBuffer byteBuffer) {
        throw new UnsupportedOperationException("Not supported");
    }
}
