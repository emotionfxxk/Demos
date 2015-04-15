package ordin.com.protocol.command;

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
    protected final static byte checkSum = 0x00;

    public byte[] getDataPacket() {
        // packet length: header(4) + length(2) + command(1) + payload(payload length) + checksum(1)
        short length = (short)(getPayloadLength() + 8);
        ByteBuffer bb = ByteBuffer.allocate(length);
        //bb.order(ByteOrder.LITTLE_ENDIAN);
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
            short length = byteBuffer.getShort();
            if( (short)(getPayloadLength() + 8) != length)
                throw new InvalidParameterException("invalid length");
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
}