package ordin.com.protocol.command;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by sean on 4/8/15.
 */
public abstract class Response extends Command {
    public static interface Creator {
        Response createInstance();
    };
    public static interface Repacker {
        Response repack(List<Response> subResponses);
        boolean needRepack(Response response);
    };

    @Override
    public short getPayloadLength() {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void fillPayload(ByteBuffer byteBuffer) {
        throw new UnsupportedOperationException("Not supported");
    }

    public static int compare(int lhs, int rhs) {
        return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
    }
}
