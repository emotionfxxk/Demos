package ordin.com.protocol.command;

import android.util.Log;
import android.util.SparseArray;

import java.nio.ByteBuffer;

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
    };

    private ResponseParser() {
    }
    public static Response parserResponse(ByteBuffer bb) {
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
