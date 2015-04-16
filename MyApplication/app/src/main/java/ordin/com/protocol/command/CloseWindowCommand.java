package ordin.com.protocol.command;

import java.nio.ByteBuffer;

/**
 * Created by sean on 4/16/15.
 */
public class CloseWindowCommand extends Response {
    public short windowId;

    public CloseWindowCommand() {
        this.command = CommandDefs.CMD_CLOSE_WINDOW;
    }
    public CloseWindowCommand(short windowId) {
        this.command = CommandDefs.CMD_CLOSE_WINDOW;
        this.windowId = windowId;
    }

    public static Creator sCreator = new Creator() {
        @Override
        public Response createInstance() {
            return new CloseWindowCommand();
        }
    };

    @Override
    public short getPayloadLength() {
        return (short)2;
    }

    @Override
    public void fillPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(windowId);
    }

    @Override
    public void parsePayload(ByteBuffer byteBuffer) {
        this.windowId = byteBuffer.getShort();
    }
}
