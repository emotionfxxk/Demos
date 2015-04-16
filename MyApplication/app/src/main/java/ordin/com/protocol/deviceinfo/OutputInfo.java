package ordin.com.protocol.deviceinfo;

/**
 * Created by sean on 4/16/15.
 */
public class OutputInfo {
    public final static int RES_1920x1080 = 1;
    public final static int RES_1366x768 = 2;
    public int outputIndex;
    public int outputPortIndex;
    public int outputResolution;
    public int boardAddress;
    public boolean isInPoll;
}
