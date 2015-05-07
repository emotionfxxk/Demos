package ordin.com.protocol.ballcontrol;

/**
 * Created by sean on 5/3/15.
 */
public class BallControlAction {
    public final static byte NONE = 0x00;
    public final static byte MOVE_LEFT = 0x01;
    public final static byte MOVE_UP = 0x02;
    public final static byte MOVE_RIGHT = 0x03;
    public final static byte MOVE_DOWN = 0x04;
    public final static byte ZOOM_IN = 0x05;
    public final static byte ZOOM_OUT = 0x06;

    public byte action;
    public BallControlAction(byte action) {
        this.action = action;
    }

    @Override
    public String toString() {
        switch (action) {
            case NONE:
                return "Event: NONE";
            case MOVE_LEFT:
                return "Event: LEFT";
            case MOVE_UP:
                return "Event: UP";
            case MOVE_RIGHT:
                return "Event: RIGHT";
            case MOVE_DOWN:
                return "Event: DOWN";
            case ZOOM_IN:
                return "Event: ZOOM IN";
            case ZOOM_OUT:
                return "Event: ZOOM OUT";
            default:
                return "Event: UNKNOWN";
        }
    }
}
