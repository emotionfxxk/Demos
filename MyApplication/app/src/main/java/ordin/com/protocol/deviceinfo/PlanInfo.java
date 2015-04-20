package ordin.com.protocol.deviceinfo;

/**
 * Created by sean on 4/16/15.
 */
public class PlanInfo {
    public int index;
    public String planName;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("index:").append(index).append(" planName:").append(planName);
        return sb.toString();
    }
}
