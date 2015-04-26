package ordin.com.protocol.command;

import java.nio.ByteBuffer;

/**
 * Created by sean on 4/16/15.
 */
public class Notification extends Response {
    private Notification() {}

    public static Response.Creator sCreator = new Response.Creator() {
        @Override
        public Response createInstance() {
            return new Notification();
        }
    };
    @Override
    public void parsePayload(ByteBuffer byteBuffer, int payloadLength) {
    }
}
