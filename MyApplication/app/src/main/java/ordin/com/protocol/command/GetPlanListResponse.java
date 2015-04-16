package ordin.com.protocol.command;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import ordin.com.protocol.deviceinfo.OutputInfo;
import ordin.com.protocol.deviceinfo.PlanInfo;

/**
 * Created by sean on 4/16/15.
 */
public class GetPlanListResponse extends Response {
    public boolean isChanged;
    public int totalCount;
    public int index;
    public int planCount;
    public PlanInfo[] planInfos;

    private GetPlanListResponse() {}

    public static Creator sCreator = new Creator() {
        @Override
        public Response createInstance() {
            return new GetPlanListResponse();
        }
    };

    @Override
    public void parsePayload(ByteBuffer byteBuffer) {
        isChanged = (byteBuffer.get() == 0x01);
        totalCount = byteBuffer.get();
        index = byteBuffer.get();
        planCount = byteBuffer.get();
        planInfos = new PlanInfo[planCount];
        for(int planIndex = 0; planIndex < planCount; ++planIndex) {
            PlanInfo planInfo = new PlanInfo();
            //load info
            planInfo.index = byteBuffer.get();
            int planNameLength = byteBuffer.get();;
            byte[] nameByte = new byte[planNameLength];
            byteBuffer.get(nameByte);
            planInfo.planName = new String(nameByte, Charset.forName("UTF-8"));

            planInfos[planIndex] = planInfo;
        }
    }
}
