package ordin.com.protocol.command;

import java.nio.ByteBuffer;

/**
 * Created by sean on 4/16/15.
 */
public class MoveWindowCommand extends Response {
    public short windowId;
    public short userZOrder;
    public short left, top;
    public short width, height;
    public short leftTop, rightBottom;

    public MoveWindowCommand() {
        this.command = CommandDefs.CMD_MOVE_WINDOW;
    }
    public MoveWindowCommand(short windowId, short userZOrder, short left, short top,
                             short width, short height, short leftTop, short rightBottom) {
        this.command = CommandDefs.CMD_MOVE_WINDOW;
        this.windowId = windowId;
        this.userZOrder = userZOrder;
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
        this.leftTop = leftTop;
        this.rightBottom = rightBottom;
    }

    public static Creator sCreator = new Creator() {
        @Override
        public Response createInstance() {
            return new MoveWindowCommand();
        }
    };

    @Override
    public short getPayloadLength() {
        return (short)16;
    }

    @Override
    public void fillPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(windowId);
        byteBuffer.putShort(userZOrder);
        byteBuffer.putShort(left);
        byteBuffer.putShort(top);
        byteBuffer.putShort(width);
        byteBuffer.putShort(height);
        byteBuffer.putShort(leftTop);
        byteBuffer.putShort(rightBottom);
    }

    @Override
    public void parsePayload(ByteBuffer byteBuffer, int payloadLength) {
        this.windowId = byteBuffer.getShort();
        this.userZOrder = byteBuffer.getShort();
        this.left = byteBuffer.getShort();
        this.top = byteBuffer.getShort();
        this.width = byteBuffer.getShort();
        this.height = byteBuffer.getShort();
        this.leftTop = byteBuffer.getShort();
        this.rightBottom = byteBuffer.getShort();
    }
}
