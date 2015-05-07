package ordin.com.controlballactivity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import ordin.com.protocol.ballcontrol.BallActionFactory;
import ordin.com.protocol.ballcontrol.BallControlAction;


public class ControlBallActivity extends ActionBarActivity implements BallActionFactory.ActionListener {
    private final static String TAG = "Test";
    private BallActionFactory ballActionFactory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_ball);
        if(ballActionFactory == null) {
            ballActionFactory = new BallActionFactory(this);
        }
        ballActionFactory.setActionListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_control_ball, menu);
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
    public void onResume() {
        super.onResume();
        ballActionFactory.start();
    }

    @Override
    public void onStop() {
        ballActionFactory.stop();
        super.onStop();
    }

    @Override
    public void onAction(BallControlAction action) {
        Log.i(TAG, "onAction " + action);
    }
}
