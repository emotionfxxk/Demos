package ordin.com.protocol.command;


import java.nio.ByteBuffer;

/**
 * Created by sean on 4/16/15.
 */
public class CtrlBallRequest extends Request {
    public byte direction;
    public byte offset;

    public CtrlBallRequest(byte direction, byte offset) {
        this.command = CommandDefs.CMD_DEV_CTRL_BALL;
        this.direction = direction;
        this.offset = offset;
    }
    @Override
    public short getPayloadLength() {
        return 2;
    }
    @Override
    public void fillPayload(ByteBuffer byteBuffer) {
        byteBuffer.put(direction);
        byteBuffer.put(offset);
    }
}
