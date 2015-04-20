package ordin.com.protocol.command;

/**
 * Create command instance by params
 * Created by sean on 4/8/15.
 */
public class RequestFactory {
    private RequestFactory() {}
    public static Request createServiceDiscoverRequest(String ipAddress, int port) {
        return new ServiceDiscoverRequest(ipAddress, port);
    }

    public static Request createGetWindowStructureRequest() {
        return new CommandOnlyRequest(CommandDefs.CMD_GET_PLAN_WINDOW_STRUCTURE);
    }

    public static Request createGetInputInfoRequest() {
        return new CommandOnlyRequest(CommandDefs.CMD_GET_INPUT_INFO);
    }

    public static Request createGetIpListRequest() {
        return new CommandOnlyRequest(CommandDefs.CMD_GET_IP_LIST);
    }

    public static Request createGetOutputInfoRequest() {
        return new CommandOnlyRequest(CommandDefs.CMD_GET_OUTPUT_INFO);
    }

    public static Request createGetPlanListRequest() {
        return new CommandOnlyRequest(CommandDefs.CMD_GET_PLAN_LIST);
    }

    public static Request createGetPlanWindowInfoRequest() {
        return new CommandOnlyRequest(CommandDefs.CMD_GET_PLAN_WINDOW_INFO);
    }

    public static Request createGetPlanWindowListRequest() {
        return new CommandOnlyRequest(CommandDefs.CMD_GET_PLAN_WINDOW_LIST);
    }

    public static Command createNewWindowRequest() {
        return new CreateWindowCommand();
    }

    public static Command createMoveWindowRequest(short windowId, short userZOrder, short left, short top,
                                                  short width, short height, short leftTop, short rightBottom) {
        return new MoveWindowCommand(windowId, userZOrder, left, top, width, height, leftTop, rightBottom);
    }

    public static Command createNewWindowRequest(short windowId) {
        return new CloseWindowCommand(windowId);
    }

    public static Command createPowerControlCommand(boolean powerOn, byte devId) {
        return new PowerControlCommand(powerOn, devId);
    }

    public static Request createJpgRequest(byte imageType, boolean isOpenSubsequentImg,
                                           short resolutionX, short resolutionY, byte[] signals) {
        return new JpgRequest(imageType, isOpenSubsequentImg, resolutionX, resolutionY, signals);
    }

    public static Request createControlBallRequest(byte direction, byte offset) {
        return new CtrlBallRequest(direction, offset);
    }
}
