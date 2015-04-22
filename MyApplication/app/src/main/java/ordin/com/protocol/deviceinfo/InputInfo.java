package ordin.com.protocol.deviceinfo;

/**
 * Created by sean on 4/15/15.
 */
public class InputInfo {
    public int inputIndex;
    public int portInputIndex;      // not for mobile client
    public int inputType;           // not for mobile client
    public String inputName;
    public String inputOverlapInfo;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("inputIndex:").append(inputIndex).append(", inputName:").append(inputName)
                .append(", inputOverlapInfo:").append(inputOverlapInfo);
        return sb.toString();
    }
}
