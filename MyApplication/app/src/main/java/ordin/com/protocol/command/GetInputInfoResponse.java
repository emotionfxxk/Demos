package ordin.com.protocol.command;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ordin.com.protocol.deviceinfo.InputInfo;

/**
 * Created by sean on 4/15/15.
 */
public class GetInputInfoResponse extends Response {
    public boolean isChanged;
    public int totalCount;
    public int index;
    public int inputCount;
    //public InputInfo[] inputInfos;
    public List<InputInfo> inputInfos;
    private GetInputInfoResponse() {}

    public static Response.Creator sCreator = new Response.Creator() {
        @Override
        public Response createInstance() {
            return new GetInputInfoResponse();
        }
    };

    public static Repacker sRepacker = new Repacker() {
        @Override
        public Response repack(List<Response> subResponses) {
            GetInputInfoResponse firstResponse = (GetInputInfoResponse)subResponses.get(0);
            if(firstResponse.totalCount > subResponses.size()) return null;
            List<InputInfo> inputInfos = new ArrayList<InputInfo>();
            for(Response r : subResponses) {
                GetInputInfoResponse sr = (GetInputInfoResponse) r;
                inputInfos.addAll(sr.inputInfos);
            }
            firstResponse.inputInfos = inputInfos;
            return firstResponse;
        }
        @Override
        public boolean needRepack(Response response) {
            return (response instanceof GetInputInfoResponse) && (((GetInputInfoResponse)response).totalCount > 1);
        }
    };

    @Override
    public void parsePayload(ByteBuffer byteBuffer, int payloadLength) {
        isChanged = (byteBuffer.get() == 0x01);
        totalCount = byteBuffer.get();
        index = byteBuffer.get();
        inputCount = byteBuffer.get();
        inputInfos = new ArrayList<InputInfo>();

        int inputInfosLength = payloadLength - 4;

        while(inputInfosLength > 0) {
            InputInfo inputInfo = new InputInfo();
            //load info
            inputInfo.inputIndex = byteBuffer.get();
            inputInfo.portInputIndex = byteBuffer.get();
            inputInfo.inputType = byteBuffer.get();
            // get input name
            int nameLength = byteBuffer.get();
            byte[] nameArray = new byte[nameLength];
            byteBuffer.get(nameArray);
            inputInfo.inputName = new String(nameArray, Charset.forName("UTF-8"));
            // get overlap info
            int overlapInfoLength = byteBuffer.get();
            byte[] overlapInfoArray = new byte[overlapInfoLength];
            byteBuffer.get(overlapInfoArray);
            inputInfo.inputOverlapInfo = new String(overlapInfoArray, Charset.forName("UTF-8"));

            inputInfos.add(inputInfo);
            inputInfosLength -= (5 + nameLength + overlapInfoLength);
        }
    }
}
