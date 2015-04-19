package ordin.com.protocol.command;

import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.MalformedInputException;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.IllegalFormatException;

/**
 * Created by sean on 4/8/15.
 */
public abstract class Command {
    protected final static byte[] header = new byte[] {'O', 'd', 'i', 'n'};
    public byte command;
    public short length;
    protected final static byte checkSum = 0x00;

    public short getOverheadLength() {
        //header(4) + length(2) + command(1) +  checksum(1)
        return 8;
    }

    public byte[] getDataPacket() {
        // packet length: header(4) + length(2) + command(1) + payload(payload length) + checksum(1)
        short length = (short)(getPayloadLength() + getOverheadLength());
        ByteBuffer bb = ByteBuffer.allocate(length);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        // put header
        bb.put(header);
        // put length
        bb.putShort(length);
        // put command
        bb.put(command);
        // put command parameters
        fillPayload(bb);
        // put checksum
        bb.put(checkSum);
        return bb.array();
    }

    public abstract short getPayloadLength();
    public abstract void fillPayload(ByteBuffer byteBuffer);

    public static byte getCommand(ByteBuffer byteBuffer) throws InvalidParameterException {
        if(byteBuffer == null) throw new InvalidParameterException("byteBuffer should not be null");
        byte command = 0x00;
        try {
            byteBuffer.mark();

            // skip 4 byte header
            byteBuffer.get(new byte[4]);

            // get 2 byte length
            byteBuffer.getShort();

            command = byteBuffer.get();

            byteBuffer.reset();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidParameterException(e.getMessage());
        }
        return command;
    }

    public void parse(ByteBuffer byteBuffer) {
        if(byteBuffer == null) throw new InvalidParameterException("byteBuffer should not be null");
        try {
            // get header
            byte[] inHeader = new byte[4];
            byteBuffer.get(inHeader);
            if (!Arrays.equals(header, inHeader))
                throw new InvalidParameterException("invalid header");
            // get 2 byte length
            length = byteBuffer.getShort();

            // get command
            command = byteBuffer.get();

            parsePayload(byteBuffer);

            // get checksum
            byte inCheckSum = byteBuffer.get();
            if(inCheckSum != checkSum)
                throw new InvalidParameterException("invalid checksum");
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidParameterException(e.getMessage());
        }
    }
    public abstract void parsePayload(ByteBuffer byteBuffer);

    public static ByteBuffer readOneCommand(InputStream is) throws IOException {
        // read header and length first
        byte[] headerAndLength = new byte[6];
        is.read(headerAndLength);

        ByteBuffer bb = ByteBuffer.wrap(headerAndLength);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        bb.get(new byte[4]); // skip header
        short length = bb.getShort();

        byte[] commandPacket = new byte[length];
        System.arraycopy(headerAndLength, 0, commandPacket, 0, 6);

        is.read(commandPacket, 6, (length - 6));
        return ByteBuffer.wrap(commandPacket);
    }
}
