package ordin.com.protocol.command;


import java.nio.ByteBuffer;

/**
 * Created by sean on 4/16/15.
 */
public class JpgRequest extends Request {
    public byte imageType;
    public byte openSubsequentImg;
    public short resolutionX;
    public short resolutionY;
    public byte[] signals;

    public JpgRequest(byte imageType, boolean isOpenSubsequentImg, short resolutionX, short resolutionY, byte[] signals) {
        this.command = CommandDefs.CMD_GET_JPG;
        this.imageType = imageType;
        this.openSubsequentImg = isOpenSubsequentImg ? (byte)0x01 : (byte)0x00;
        this.resolutionX = resolutionX;
        this.resolutionY = resolutionY;
        this.signals = signals;
    }
    @Override
    public short getPayloadLength() {
        return (short)(6 + signals.length);
    }
    @Override
    public void fillPayload(ByteBuffer byteBuffer) {
        byteBuffer.put(imageType);
        byteBuffer.put(openSubsequentImg);
        byteBuffer.putShort(resolutionX);
        byteBuffer.putShort(resolutionY);
        for(byte signal : signals)
            byteBuffer.put(signal);
    }
}
