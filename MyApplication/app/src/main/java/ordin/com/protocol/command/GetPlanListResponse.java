package ordin.com.protocol.command;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

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
    public List<PlanInfo> planInfos;

    private GetPlanListResponse() {}

    public static Creator sCreator = new Creator() {
        @Override
        public Response createInstance() {
            return new GetPlanListResponse();
        }
    };
    public static Repacker sRepacker = new Repacker() {
        @Override
        public Response repack(List<Response> subResponses) {
            GetPlanListResponse firstResponse = (GetPlanListResponse)subResponses.get(0);
            if(firstResponse.totalCount > subResponses.size()) return null;

            List<PlanInfo> planInfos = new ArrayList<PlanInfo>();
            for(Response r : subResponses) {
                GetPlanListResponse sr = (GetPlanListResponse) r;
                planInfos.addAll(sr.planInfos);
            }
            firstResponse.planInfos = planInfos;
            return firstResponse;
        }
        @Override
        public boolean needRepack(Response response) {
            return (response instanceof GetPlanListResponse) && (((GetPlanListResponse)response).totalCount > 1);
        }
    };

    @Override
    public void parsePayload(ByteBuffer byteBuffer, int payloadLength) {
        isChanged = (byteBuffer.get() == 0x01);
        totalCount = byteBuffer.get();
        index = byteBuffer.get();
        planCount = byteBuffer.get();
        planInfos = new ArrayList<PlanInfo>();
        int planInfosLength = payloadLength - 4;

        while (planInfosLength > 0) {
            PlanInfo planInfo = new PlanInfo();
            //load info
            planInfo.index = byteBuffer.get();
            int planNameLength = byteBuffer.get();;
            byte[] nameByte = new byte[planNameLength];
            byteBuffer.get(nameByte);
            planInfo.planName = new String(nameByte, Charset.forName("UTF-8"));
            planInfosLength -= (2 + planNameLength);
            planInfos.add(planInfo);
        }
    }
}
