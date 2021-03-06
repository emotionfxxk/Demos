package ordin.com.protocol;

import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import ordin.com.protocol.command.Command;
import ordin.com.protocol.command.GetInputInfoResponse;
import ordin.com.protocol.command.GetOutputInfoResponse;
import ordin.com.protocol.command.GetPlanListResponse;
import ordin.com.protocol.command.GetPlanWindowInfoResponse;
import ordin.com.protocol.command.GetPlanWindowListResponse;
import ordin.com.protocol.command.GetWindowStructureResponse;
import ordin.com.protocol.command.JpgResponse;
import ordin.com.protocol.command.Request;
import ordin.com.protocol.command.RequestFactory;
import ordin.com.protocol.connection.CommandListener;
import ordin.com.protocol.connection.ConnectionManager;
import ordin.com.protocol.connection.ControlConnection;
import ordin.com.protocol.connection.JpgConnection;
import ordin.com.protocol.deviceinfo.InputInfo;
import ordin.com.protocol.deviceinfo.OutputInfo;
import ordin.com.protocol.deviceinfo.PlanInfo;
import ordin.com.protocol.deviceinfo.ScreenGroup;
import ordin.com.protocol.image.ImageUpdater;


public class OrdinProtocalTest extends ActionBarActivity {
    private final static String TAG = "OrdinProtocalTest";
    private Handler handler;
    private Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what == ConnectionManager.MSG_ON_CTRL_CON_CONNECTED) {
                onGetControlConnection();
            }
            return true;
        }
    };

    private ImageView testImageView;
    private ImageUpdater imageUpdater = new ImageUpdater();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordin_protocal_test);
        testImageView = (ImageView)findViewById(R.id.test_image);
        imageUpdater.subscribe(0, testImageView);
        handler = new Handler(callback);
        ConnectionManager.defaultManager.connect(handler, this);
    }

    private void onGetControlConnection() {
        Log.i(TAG, "onGetControlConnection");
        ControlConnection con = ConnectionManager.defaultManager.getControlConnection();
        con.addCommandListener(commandListener);

        { // test get window structure
            Request req = RequestFactory.createGetWindowStructureRequest();
            con.sendCommand(req);
        }

        { // test get input info
            Request req = RequestFactory.createGetInputInfoRequest();
            con.sendCommand(req);
        }

        { // test get output info
            Request req = RequestFactory.createGetOutputInfoRequest();
            con.sendCommand(req);
        }

        { // test get plan list
            Request req = RequestFactory.createGetPlanListRequest();
            con.sendCommand(req);
        }

        { // test get plan window list
            Request req = RequestFactory.createGetPlanWindowListRequest();
            con.sendCommand(req);
        }

        { // 获得预案窗口信息
            Request req = RequestFactory.createGetPlanWindowInfoRequest();
            con.sendCommand(req);
        }
        /*
        {
            Request req = RequestFactory.createJpgRequest(CommandDefs.PARAM_SIGNAL_IMAGE, true,
                    (short)480, (short)270, new byte[]{0x05});
            con.sendCommand(req);
        }*/
    }



    CommandListener commandListener = new CommandListener() {
        @Override
        public void onSentCommand(Command cmd) {
            Log.i(TAG, "onSentCommand:" + cmd.command);
        }

        @Override
        public void onReceivedCommand(Command cmd) {
            Log.i(TAG, "onReceivedCommand:" + cmd);
            if (cmd instanceof GetWindowStructureResponse) {
                GetWindowStructureResponse r = (GetWindowStructureResponse) cmd;
                for (ScreenGroup sg : r.screenGroups) {
                    Log.i(TAG, "get window structure, sg:" + sg);
                }
            } else if(cmd instanceof GetPlanListResponse) {
                GetPlanListResponse r = (GetPlanListResponse) cmd;
                for(PlanInfo pi : r.planInfos) {
                    Log.i(TAG, "get plan info, pi:" + pi);
                }
            } else if(cmd instanceof GetPlanWindowListResponse) {
                GetPlanWindowListResponse r = (GetPlanWindowListResponse) cmd;
                Log.i(TAG, "GetPlanWindowListResponse list sg window count:" + r.windowCount);
                for (ScreenGroup sg : r.screenGroups) {
                    Log.i(TAG, "get plan window list sg:" + sg);
                }
            } else if(cmd instanceof GetInputInfoResponse) {
                GetInputInfoResponse r = (GetInputInfoResponse) cmd;
                for (InputInfo ii : r.inputInfos) {
                    Log.i(TAG, "get input info, ii:" + ii);
                }
                ConnectionManager.defaultManager.startJpgTransport(imageUpdater,
                        (short)480, (short)270, new byte[]{0x00});
            } else if(cmd instanceof GetOutputInfoResponse) {
                GetOutputInfoResponse r =(GetOutputInfoResponse)cmd;
                for (OutputInfo oi : r.outputInfos) {
                    Log.i(TAG, "get output info, oi:" + oi);
                }
            } else if(cmd instanceof GetPlanWindowInfoResponse) {
                GetPlanWindowInfoResponse r = (GetPlanWindowInfoResponse) cmd;
                for (ScreenGroup sg : r.screenGroups) {
                    Log.i(TAG, "get plan window info sg:" + sg);
                }
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        // release connection before go to background
        ConnectionManager.defaultManager.disconnect();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ordin_protocal_test, menu);
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
}

