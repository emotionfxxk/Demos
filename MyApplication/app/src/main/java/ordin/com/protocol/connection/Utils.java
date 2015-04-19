package ordin.com.protocol.connection;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Created by sean on 4/17/15.
 */
public class Utils {
    private Utils() {}
    public static String getLocalIpAddress(Context ctx) {
        WifiManager wifiManager = (WifiManager)ctx.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        return ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "." + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
    }
}
