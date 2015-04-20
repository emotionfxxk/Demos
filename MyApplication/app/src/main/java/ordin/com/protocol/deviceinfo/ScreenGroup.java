package ordin.com.protocol.deviceinfo;

/**
 * Created by sean on 4/15/15.
 */
public class ScreenGroup {
    public int horizontalCount;
    public int verticalCount;
    public int startNumber;
    public String description;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("hCount:").append(horizontalCount).append(" vCount:").append(verticalCount)
                .append(" startNo:").append(startNumber).append(" desc:").append(description);
        return sb.toString();
    }
}
