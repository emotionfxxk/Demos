package ordin.com.protocol.command;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
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
    private final static String TAG = "Command";
    private final static int BUF_SIZE = 1024 * 2;
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
            int totalLength = byteBuffer.remaining();
            // get header
            byte[] inHeader = new byte[4];
            byteBuffer.get(inHeader);
            if (!Arrays.equals(header, inHeader))
                throw new InvalidParameterException("invalid header");
            // get 2 byte length
            length = byteBuffer.getShort();

            // get command
            command = byteBuffer.get();

            // parse payload beside command byte(total - 4byte header - 2 byte length - 1 byte cmd
            // - 1 byte checksum)
            parsePayload(byteBuffer, totalLength - 8);

            // get checksum
            byte inCheckSum = byteBuffer.get();
            if(inCheckSum != checkSum)
                throw new InvalidParameterException("invalid checksum:" + inCheckSum);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidParameterException(e.getMessage());
        }
    }
    public abstract void parsePayload(ByteBuffer byteBuffer, int payloadLength);

    // for tcp connection
    public static ByteBuffer readOneCommand(InputStream is) throws IOException {
        // read header and length first
        byte[] headerAndLength = new byte[6];
        is.read(headerAndLength);

        ByteBuffer bb = ByteBuffer.wrap(headerAndLength);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        bb.get(new byte[4]); // skip header
        short length = bb.getShort();
        Log.i(TAG, "command packet length:" + length);
        byte[] commandPacket = new byte[length];
        System.arraycopy(headerAndLength, 0, commandPacket, 0, 6);

        is.read(commandPacket, 6, (length - 6));
        return ByteBuffer.wrap(commandPacket);
    }

    // for udp connection
    public static ByteBuffer readOneCommand(DatagramSocket socket) throws IOException {
        while(true) {
            byte[] datagram = new byte[BUF_SIZE];
            DatagramPacket packet = new DatagramPacket(datagram, datagram.length);
            socket.receive(packet);

            ByteBuffer bb = ByteBuffer.wrap(datagram);
            bb.order(ByteOrder.LITTLE_ENDIAN);

            bb.get(new byte[4]); // skip header
            short length = bb.getShort();
            if(length == 0) {
                Log.i(TAG, "redundant package :packet.getLength():" + packet.getLength() + ", length in pack:" + length);
            } else {
                return ByteBuffer.wrap(datagram);
            }
        }
    }
}
