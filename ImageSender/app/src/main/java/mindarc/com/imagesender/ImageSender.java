package mindarc.com.imagesender;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class ImageSender extends ActionBarActivity implements View.OnClickListener  {
    private final static String TAG = "ImageSender";
    private TextView mServerInfo;
    private Button mStartStopBtn;
    //private ImageSenderService mImageService;
    private DataSource mDataSource;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_sender);
        //mImageService = new ImageSenderService(getAssets());
        mDataSource = new DataSource();
        DataSink sink = new DataSink(getAssets());
        mDataSource.setDataSink(sink);
        mServerInfo = (TextView) findViewById(R.id.server_info);
        mStartStopBtn = (Button) findViewById(R.id.btn_start_stop);
        mStartStopBtn.setOnClickListener(this);
        mServerInfo.setText("Server IP Address:----");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_sender, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onClick(View v) {
        if(mDataSource.isStarted()) {
            mDataSource.stop();
            mServerInfo.setText("Server IP Address:----");
        } else {
            mDataSource.start();
            mServerInfo.setText("Server IP Address:" + getLocalIpAddress());
        }
        updateStartStopBtn();
    }
    private void updateStartStopBtn() {
        mStartStopBtn.setText(!mDataSource.isStarted() ? R.string.btn_start : R.string.btn_stop);
    }
    private String getLocalIpAddress() {
        WifiManager wifiManager = (WifiManager)getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        Log.d(TAG, "int ip " + ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "." + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff)));
        return ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "." + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
    }
}
