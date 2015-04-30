package ordin.com.protocol.command;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import ordin.com.protocol.deviceinfo.OutputInfo;
import ordin.com.protocol.deviceinfo.ScreenGroup;

/**
 * Created by sean on 4/16/15.
 */
public class GetPlanWindowListResponse extends Response {
    public int totalCount;
    public int index;
    public int windowCount;
    public List<ScreenGroup> screenGroups;

    private GetPlanWindowListResponse() {}

    public static Creator sCreator = new Creator() {
        @Override
        public Response createInstance() {
            return new GetPlanWindowListResponse();
        }
    };
    public static Repacker sRepacker = new Repacker() {
        @Override
        public Response repack(List<Response> subResponses) {
            GetPlanWindowListResponse firstResponse = (GetPlanWindowListResponse)subResponses.get(0);
            if(firstResponse.totalCount > subResponses.size()) return null;

            List<ScreenGroup> screenGroups = new ArrayList<ScreenGroup>();
            for(Response r : subResponses) {
                GetPlanWindowListResponse sr = (GetPlanWindowListResponse) r;
                screenGroups.addAll(sr.screenGroups);
            }
            firstResponse.screenGroups = screenGroups;
            return firstResponse;
        }
        @Override
        public boolean needRepack(Response response) {
            return (response instanceof GetPlanWindowListResponse) && (((GetPlanWindowListResponse)response).totalCount > 1);
        }
    };

    @Override
    public void parsePayload(ByteBuffer byteBuffer, int payloadLength) {
        totalCount = byteBuffer.get();
        index = byteBuffer.get();
        windowCount = byteBuffer.get();

        screenGroups = new ArrayList<ScreenGroup>();
        int screenGroupLength = payloadLength - 3;

        while(screenGroupLength > 0) {
            ScreenGroup sg = new ScreenGroup();
            sg.horizontalCount = byteBuffer.get();
            sg.verticalCount = byteBuffer.get();
            sg.startNumber = byteBuffer.get();
            // get description
            // TODO: make sure the description is no longer than 256 bytes
            byte[] descriptionBuffer = new byte[256];
            byte singleChar;
            int charIndex = 0;
            do {
                singleChar = byteBuffer.get();
                if(singleChar != '\0')
                    descriptionBuffer[charIndex++] = singleChar;
                else
                    break;
            } while(true);
            sg.description = new String(descriptionBuffer, 0, charIndex, Charset.forName("UTF-8"));
            screenGroupLength -= (3 + charIndex);
            screenGroups.add(sg);
        }
    }
}
