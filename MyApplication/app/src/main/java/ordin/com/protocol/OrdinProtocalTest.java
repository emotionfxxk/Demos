package ordin.com.protocol;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import ordin.com.protocol.command.Command;
import ordin.com.protocol.command.CommandDefs;
import ordin.com.protocol.command.GetPlanListResponse;
import ordin.com.protocol.command.GetPlanWindowListResponse;
import ordin.com.protocol.command.GetWindowStructureResponse;
import ordin.com.protocol.command.JpgResponse;
import ordin.com.protocol.command.Request;
import ordin.com.protocol.command.RequestFactory;
import ordin.com.protocol.connection.CommandListener;
import ordin.com.protocol.connection.ConnectionManager;
import ordin.com.protocol.connection.ControlConnection;
import ordin.com.protocol.connection.JpgConnection;
import ordin.com.protocol.deviceinfo.PlanInfo;
import ordin.com.protocol.deviceinfo.ScreenGroup;


public class OrdinProtocalTest extends ActionBarActivity {
    private final static String TAG = "OrdinProtocalTest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordin_protocal_test);

        ConnectionManager.defaultManager.startDiscovery(this);
        ConnectionManager.defaultManager.setOnGetConnection(new ConnectionManager.OnGetConnection() {
            @Override
            public void onGetControlConnection(ControlConnection con) {
                Log.i(TAG, "onGetControlConnection");
                con.addCommandListener(commandListener);

                { // test get window structure
                    Request req = RequestFactory.createGetWindowStructureRequest();
                    con.sendCommand(req);
                }
                /*
                { // test get window structure
                    Request req = RequestFactory.createGetWindowStructureRequest();
                    con.sendCommand(req);
                }

                { // test get plan list
                    Request req = RequestFactory.createGetPlanListRequest();
                    con.sendCommand(req);
                }

                { // test get plan window list
                    Request req = RequestFactory.createGetPlanWindowListRequest();
                    con.sendCommand(req);
                }*/

                {
                    Request req = RequestFactory.createJpgRequest(CommandDefs.PARAM_SIGNAL_IMAGE, true,
                            (short)480, (short)270, new byte[]{0x05});
                    con.sendCommand(req);
                }

            }

            @Override
            public void onGetJpgConnection(JpgConnection con) {
                Log.i(TAG, "onGetJpgConnection");
                con.addCommandListener(commandListener);
            }
        });
    }

    CommandListener commandListener = new CommandListener() {
        @Override
        public void onSentCommand(Command cmd) {
            Log.i(TAG, "onSentCommand");
        }

        @Override
        public void onReceivedCommand(Command cmd) {
            Log.i(TAG, "onReceivedCommand:" + cmd);
            if (cmd instanceof GetWindowStructureResponse) {
                GetWindowStructureResponse r = (GetWindowStructureResponse) cmd;
                for (ScreenGroup sg : r.screenGroups) {
                    Log.i(TAG, "sg:" + sg);
                }
            } else if(cmd instanceof GetPlanListResponse) {
                GetPlanListResponse r = (GetPlanListResponse) cmd;
                for(PlanInfo pi : r.planInfos) {
                    Log.i(TAG, "pi:" + pi);
                }
            } else if(cmd instanceof GetPlanWindowListResponse) {
                GetPlanWindowListResponse r = (GetPlanWindowListResponse) cmd;
                for (ScreenGroup sg : r.screenGroups) {
                    Log.i(TAG, "sg:" + sg);
                }
            } else if(cmd instanceof JpgResponse) {
                JpgResponse r = (JpgResponse) cmd;
                Log.i(TAG, "total pkg:" + r.totalCount + ", index:" + r.index + ", data:" + r.imageData.length);
            }
        }
    };

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

