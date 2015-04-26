package ordin.com.protocol.command;

import java.nio.ByteBuffer;

/**
 * Created by sean on 4/17/15.
 */
public class JpgResponse extends Response {
    public byte imageType;
    public int planIndex;
    public int totalCount;
    public int index;
    public byte[] imageData;

    public static Creator sCreator = new Creator() {
        @Override
        public Response createInstance() {
            return new JpgResponse();
        }
    };

    private JpgResponse() {}

    @Override
    public void parsePayload(ByteBuffer byteBuffer, int payloadLength) {
        // get image type
        imageType = byteBuffer.get();
        planIndex = (byteBuffer.get() & 0x000000FF);
        totalCount = (byteBuffer.get() & 0x000000FF);
        index = (byteBuffer.get() & 0x000000FF);
        imageData = new byte[length - getOverheadLength() - 4];
        byteBuffer.get(imageData);
    }
}
