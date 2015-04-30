package ordin.com.protocol.command;

import android.util.SparseArray;

import java.util.ArrayList;

/**
 * Created by sean on 4/24/15.
 */
public class ResponseRepacker {
    public final static ResponseRepacker defaultRepacker = new ResponseRepacker();

    private final static SparseArray<Response.Repacker> REPACKER_MAP = new SparseArray<Response.Repacker>();
    static {
        REPACKER_MAP.put(CommandDefs.CMD_GET_INPUT_INFO, GetInputInfoResponse.sRepacker);
        REPACKER_MAP.put(CommandDefs.CMD_GET_OUTPUT_INFO, GetOutputInfoResponse.sRepacker);
        REPACKER_MAP.put(CommandDefs.CMD_GET_PLAN_LIST, GetPlanListResponse.sRepacker);
        REPACKER_MAP.put(CommandDefs.CMD_GET_PLAN_WINDOW_INFO, GetPlanWindowInfoResponse.sRepacker);
        REPACKER_MAP.put(CommandDefs.CMD_GET_PLAN_WINDOW_LIST, GetPlanWindowListResponse.sRepacker);
    }

    private SparseArray<ArrayList<Response>> subPacketMap = new SparseArray<ArrayList<Response>>();
    private ResponseRepacker() {}

    private boolean needRepack(Response response) {
        return (REPACKER_MAP.get(response.command) != null) && REPACKER_MAP.get(response.command).needRepack(response);
    }

    public Response repack(Response response) {
        if(needRepack(response)) {
            // do repack work here
            ArrayList<Response> subPackets = subPacketMap.get(response.command);
            if(subPackets == null) {
                subPackets = new ArrayList<Response>();
                subPacketMap.put(response.command, subPackets);
            }
            subPackets.add(response);
            Response packedResponse = REPACKER_MAP.get(response.command).repack(subPackets);
            if(packedResponse != null) {
                subPacketMap.remove(packedResponse.command);
            }
            return packedResponse;
        } else {
            return response;
        }
    }
}
