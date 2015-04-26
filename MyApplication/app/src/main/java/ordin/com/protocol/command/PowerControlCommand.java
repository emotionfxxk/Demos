package ordin.com.protocol.command;

import java.nio.ByteBuffer;

/**
 * Created by sean on 4/16/15.
 */
public class PowerControlCommand extends Response {
    public static byte DEVID_CPU = 0x01;
    public static byte DEVID_SCREEN = 0x02;
    public static byte DEVID_SCREEN_AND_CPU = 0x03;

    public byte powerOn;
    public byte devId;



    public PowerControlCommand() {
        this.command = CommandDefs.CMD_DEV_CTRL_POWER;
    }
    public PowerControlCommand(boolean powerOn, byte devId) {
        this.command = CommandDefs.CMD_DEV_CTRL_POWER;
        this.powerOn = powerOn ? (byte)0x01 : (byte)0x00;
        this.devId = devId;
    }

    public static Creator sCreator = new Creator() {
        @Override
        public Response createInstance() {
            return new PowerControlCommand();
        }
    };

    @Override
    public short getPayloadLength() {
        return (short)2;
    }

    @Override
    public void fillPayload(ByteBuffer byteBuffer) {
        byteBuffer.put(powerOn);
        byteBuffer.put(devId);
    }

    @Override
    public void parsePayload(ByteBuffer byteBuffer, int payloadLength) {
        powerOn = byteBuffer.get();
        devId = byteBuffer.get();
    }
}
