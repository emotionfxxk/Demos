package ordin.com.protocol.command;

import java.nio.ByteBuffer;

/**
 * Created by sean on 4/16/15.
 */
public class GetIpListResponse extends Response {
    private GetIpListResponse() {}

    public static Creator sCreator = new Creator() {
        @Override
        public Response createInstance() {
            return new GetIpListResponse();
        }
    };

    @Override
    public void parsePayload(ByteBuffer byteBuffer, int payloadLength) {
        // TODO: to be implemented
        throw new UnsupportedOperationException("To be implemented!");
    }
}
