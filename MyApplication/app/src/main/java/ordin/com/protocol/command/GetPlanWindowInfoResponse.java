package ordin.com.protocol.command;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;

import ordin.com.protocol.deviceinfo.InputInfo;
import ordin.com.protocol.deviceinfo.ScreenGroup;

/**
 * Created by sean on 4/16/15.
 */
public class GetPlanWindowInfoResponse extends Response {
    public int totalCount;
    public int index;
    public int windowCount;
    public int planIndex;
    public boolean isChanged;
    public ScreenGroup[] screenGroups;

    private GetPlanWindowInfoResponse() {}

    public static Creator sCreator = new Creator() {
        @Override
        public Response createInstance() {
            return new GetPlanWindowInfoResponse();
        }
    };
    public static Repacker sRepacker = new Repacker() {
        @Override
        public Response repack(List<Response> subResponses) {
            GetPlanWindowInfoResponse firstResponse = (GetPlanWindowInfoResponse)subResponses.get(0);
            if(firstResponse.totalCount > subResponses.size()) return null;

            int screenGroupCount = 0;
            for(Response r : subResponses) {
                screenGroupCount += ((GetPlanWindowInfoResponse)r).screenGroups.length;
            }

            ScreenGroup[] screenGroups = new ScreenGroup[screenGroupCount];
            int pos = 0;
            for(Response r : subResponses) {
                GetPlanWindowInfoResponse sr = (GetPlanWindowInfoResponse) r;
                for(ScreenGroup sg : sr.screenGroups) {
                    screenGroups[pos++] = sg;
                }
            }
            firstResponse.screenGroups = screenGroups;
            return firstResponse;
        }
        @Override
        public boolean needRepack(Response response) {
            return (response instanceof GetPlanWindowInfoResponse) && (((GetPlanWindowInfoResponse)response).totalCount > 1);
        }
    };

    @Override
    public void parsePayload(ByteBuffer byteBuffer, int payloadLength) {
        totalCount = byteBuffer.get();
        index = byteBuffer.get();
        windowCount = byteBuffer.get();
        planIndex = byteBuffer.get();
        isChanged = (byteBuffer.get() == 0x01);
        screenGroups = new ScreenGroup[windowCount];

        for(int pos = 0; pos < windowCount; ++pos) {
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
            screenGroups[pos] = sg;
        }
    }
}
