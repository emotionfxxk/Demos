package ordin.com.protocol.command;

import java.nio.ByteBuffer;

import ordin.com.protocol.deviceinfo.OutputInfo;

/**
 * Created by sean on 4/16/15.
 */
public class GetOutputInfoResponse extends Response {
    public int totalCount;
    public int index;
    public int outputCount;
    public OutputInfo[] outputInfos;
    private GetOutputInfoResponse() {}

    public static Creator sCreator = new Creator() {
        @Override
        public Response createInstance() {
            return new GetOutputInfoResponse();
        }
    };

    @Override
    public void parsePayload(ByteBuffer byteBuffer) {
        totalCount = byteBuffer.get();
        index = byteBuffer.get();
        outputCount = byteBuffer.get();
        outputInfos = new OutputInfo[outputCount];
        for(int outputIndex = 0; outputIndex < outputCount; ++outputIndex) {
            OutputInfo outputInfo = new OutputInfo();
            //load info
            outputInfo.outputIndex = byteBuffer.get();
            outputInfo.outputPortIndex = byteBuffer.get();
            outputInfo.outputResolution = byteBuffer.get();
            outputInfo.boardAddress = byteBuffer.get();
            outputInfo.isInPoll = (byteBuffer.get() != 0x00);
            outputInfos[outputIndex] = outputInfo;
        }
    }
}
