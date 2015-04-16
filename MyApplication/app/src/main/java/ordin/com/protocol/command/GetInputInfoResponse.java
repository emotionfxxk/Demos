package ordin.com.protocol.command;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import ordin.com.protocol.deviceinfo.InputInfo;

/**
 * Created by sean on 4/15/15.
 */
public class GetInputInfoResponse extends Response {
    public boolean isChanged;
    public int totalCount;
    public int index;
    public int inputCount;
    public InputInfo[] inputInfos;
    private GetInputInfoResponse() {}

    public static Response.Creator sCreator = new Response.Creator() {
        @Override
        public Response createInstance() {
            return new GetInputInfoResponse();
        }
    };
    @Override
    public void parsePayload(ByteBuffer byteBuffer) {
        isChanged = (byteBuffer.get() == 0x01);
        totalCount = byteBuffer.get();
        index = byteBuffer.get();
        inputCount = byteBuffer.get();
        inputInfos = new InputInfo[inputCount];
        for(int inputIndex = 0; inputIndex < inputCount; ++inputIndex) {
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

            inputInfos[inputIndex] = inputInfo;
        }
    }
}
