package ordin.com.protocol.command;

import android.util.Log;
import android.util.SparseArray;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Parse response from received data package
 * Created by sean on 4/8/15.
 */
public class ResponseParser {
    private final static String TAG = "ResponseParser";
    //public final static ResponseParser defaultParser = new ResponseParser();
    public final static SparseArray<Response.Creator> CREATOR_MAP = new SparseArray<Response.Creator>();
    static {
        // register response factory method here
        CREATOR_MAP.put(CommandDefs.CMD_DISCOVER_SERVICE, ServiceDiscoverResponse.sCreator);
        CREATOR_MAP.put(CommandDefs.CMD_GET_PLAN_WINDOW_STRUCTURE, GetWindowStructureResponse.sCreator);
        CREATOR_MAP.put(CommandDefs.CMD_GET_INPUT_INFO, GetInputInfoResponse.sCreator);
        CREATOR_MAP.put(CommandDefs.CMD_GET_IP_LIST, GetIpListResponse.sCreator);
        CREATOR_MAP.put(CommandDefs.CMD_GET_OUTPUT_INFO, GetOutputInfoResponse.sCreator);
        CREATOR_MAP.put(CommandDefs.CMD_GET_PLAN_LIST, GetPlanListResponse.sCreator);
        CREATOR_MAP.put(CommandDefs.CMD_GET_PLAN_WINDOW_INFO, GetPlanWindowInfoResponse.sCreator);
        CREATOR_MAP.put(CommandDefs.CMD_GET_PLAN_WINDOW_LIST, GetPlanWindowListResponse.sCreator);
        CREATOR_MAP.put(CommandDefs.CMD_PLAN_WINDOW_STRUCTURE_CHANGED, Notification.sCreator);
        CREATOR_MAP.put(CommandDefs.CMD_INPUT_STRUCTURE_CHANGED, Notification.sCreator);
        CREATOR_MAP.put(CommandDefs.CMD_OUTPUT_STRUCTURE_CHANGED, Notification.sCreator);

        // Bi-direction commands
        CREATOR_MAP.put(CommandDefs.CMD_CREATE_NEW_WINDOW, CreateWindowCommand.sCreator);
        CREATOR_MAP.put(CommandDefs.CMD_MOVE_WINDOW, MoveWindowCommand.sCreator);
        CREATOR_MAP.put(CommandDefs.CMD_CLOSE_WINDOW, CloseWindowCommand.sCreator);
        CREATOR_MAP.put(CommandDefs.CMD_DEV_CTRL_POWER, PowerControlCommand.sCreator);

    };

    private ResponseParser() {}
    public static Response parserResponse(ByteBuffer bb) {
        bb.order(ByteOrder.LITTLE_ENDIAN);
        Response response = null;
        byte command;
        try {
            command = Command.getCommand(bb);
            Response.Creator creator = CREATOR_MAP.get(command);
            if(creator != null) response = creator.createInstance();
            if(response != null) response.parse(bb);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to parse response" + e.getMessage());
        }
        return response;
    }
}
