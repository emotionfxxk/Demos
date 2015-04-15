package ordin.com.protocol.command;

/**
 * Created by sean on 4/8/15.
 */
public final class CommandDefs {
    private CommandDefs() {}
    /*
     * Service discovery
     */
    public final static byte CMD_DISCOVER_SERVICE = 0x00;

    /*
     * get service information
     */
    public final static byte CMD_GET_INPUT_INFO = 0x11;
    public final static byte CMD_GET_IP_LIST = 0x12;
    public final static byte CMD_GET_OUTPUT_INFO = 0x13;
    public final static byte CMD_GET_PLAN_LIST = 0x14;
    public final static byte CMD_GET_PLAN_WINDOW_INFO = 0x15;
    public final static byte CMD_GET_PLAN_WINDOW_LIST = 0x17;
    public final static byte CMD_GET_PLAN_WINDOW_STRUCTURE = 0x18;

    public final static byte CMD_PLAN_WINDOW_STRUCTURE_CHANGED = 0x20;
    public final static byte CMD_INPUT_STRUCTURE_CHANGED = 0x21;
    public final static byte CMD_OUTPUT_STRUCTURE_CHANGED = 0x22;

    public final static byte CMD_DEV_CTRL_POWER = 0x30;

    public final static byte CMD_CREATE_NEW_WINDOW = 0x40;
    public final static byte CMD_MOVE_WINDOW = 0x41;
    public final static byte CMD_CLOSE_WINDOW = 0x42;
    public final static byte CMD_ACTIVATE_WINDOW = 0x43;

    public final static byte CMD_NEW_PLAN = 0x50;
    public final static byte CMD_DEL_PLAN = 0x51;
    public final static byte CMD_RENAME_PLAN = 0x52;
    public final static byte CMD_INVOKE_PLAN = 0x53;

    public final static byte CMD_GET_JPG = 0x60;
    public final static byte CMD_TRANSFER_JPG = 0x61;

    public final static byte CMD_DEV_CTRL_BALL = 0x70;

    public final static byte CMD_FACE_DETECTION = (byte)0x80;

    //public final static byte CMD_TRANSFER_JPG = (byte)0x81;
}
