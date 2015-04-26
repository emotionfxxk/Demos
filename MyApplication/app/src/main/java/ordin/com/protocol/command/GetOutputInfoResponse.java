package ordin.com.protocol.command;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import ordin.com.protocol.deviceinfo.InputInfo;
import ordin.com.protocol.deviceinfo.OutputInfo;

/**
 * Created by sean on 4/16/15.
 */
public class GetOutputInfoResponse extends Response {
    public int totalCount;
    public int index;
    public int outputCount;
    public List<OutputInfo> outputInfos;
    private GetOutputInfoResponse() {}

    public static Creator sCreator = new Creator() {
        @Override
        public Response createInstance() {
            return new GetOutputInfoResponse();
        }
    };

    public static Repacker sRepacker = new Repacker() {
        @Override
        public Response repack(List<Response> subResponses) {
            GetOutputInfoResponse firstResponse = (GetOutputInfoResponse)subResponses.get(0);
            if(firstResponse.totalCount > subResponses.size()) return null;

            List<OutputInfo> outputInfos = new ArrayList<OutputInfo>();
            for(Response r : subResponses) {
                GetOutputInfoResponse sr = (GetOutputInfoResponse) r;
                outputInfos.addAll(sr.outputInfos);
            }
            firstResponse.outputInfos = outputInfos;
            return firstResponse;
        }
        @Override
        public boolean needRepack(Response response) {
            return (response instanceof GetOutputInfoResponse) && (((GetOutputInfoResponse)response).totalCount > 1);
        }
    };
    @Override
    public void parsePayload(ByteBuffer byteBuffer, int payloadLength) {
        totalCount = byteBuffer.get();
        index = byteBuffer.get();
        outputCount = byteBuffer.get();
        outputInfos = new ArrayList<OutputInfo>();
        int outputInfoLength = payloadLength - 3;
        while(outputInfoLength > 0) {
            OutputInfo outputInfo = new OutputInfo();
            //load info
            outputInfo.outputIndex = byteBuffer.get();
            outputInfo.outputPortIndex = byteBuffer.get();
            outputInfo.outputResolution = byteBuffer.get();
            outputInfo.boardAddress = byteBuffer.get();
            outputInfo.isInPoll = (byteBuffer.get() != 0x00);
            outputInfoLength -= 5;
            outputInfos.add(outputInfo);
        }
    }
}
