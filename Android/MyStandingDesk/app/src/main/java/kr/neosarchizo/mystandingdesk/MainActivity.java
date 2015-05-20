package kr.neosarchizo.mystandingdesk;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;


public class MainActivity extends Activity {


    private static final String TAG = "MainActivity";

    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BTService mBTService = null;

    private EventBus mEventBus = EventBus.getDefault();

    @InjectView(R.id.btnUp)
    View btnUp;

    @InjectView(R.id.btnDown)
    View btnDown;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        btnUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        sendCommand("d");
                        break;
                    case MotionEvent.ACTION_UP:
                        sendCommand("s");
                        break;
                }
                return true;
            }
        });

        btnDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        sendCommand("a");
                        break;
                    case MotionEvent.ACTION_UP:
                        sendCommand("s");
                        break;
                }
                return true;
            }
        });

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.bt_not_available, Toast.LENGTH_LONG).show();
            finish();
        }

        mEventBus.register(this);
    }

    private void sendCommand(String command){
        if (mBTService.getState() != mBTService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (command.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = command.getBytes();
            mBTService.write(send);;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (mBTService == null) {
            setupBT();
        }
    }

    @Override
    protected void onDestroy() {
        if (mBTService != null) {
            mBTService.stop();
        }

        mEventBus.unregister(this);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mBTService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mBTService.getState() == mBTService.STATE_NONE) {
                // Start the Bluetooth chat services
                BluetoothDevice bluetoothDevice =  mBluetoothAdapter.getRemoteDevice("20:14:08:28:09:01");
                mBTService.connect(bluetoothDevice);
            }
        }
    }

    /**
     * Set up the UI and background operations for BT.
     */
    private void setupBT() {
        Log.d(TAG, "setupBT()");

        // Initialize the BluetoothChatService to perform bluetooth connections
        mBTService = new BTService(this);
    }

    public void onEvent(BTServiceEvent btServiceEvent) {
        switch (btServiceEvent.getEvent()) {
            case NONE:
                break;
            case CONNECTING:
                break;
            case CONNECTED:
                break;
            case CONNECTION_FAIL:
                break;
            case CONNECTION_LOST:
                break;
            case DATA_READ:
                String readData = new String(btServiceEvent.getBuffer(), 0, btServiceEvent.getBufferSize());
                break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.set_height: {
                // TODO set height of Desk
                return true;
            }
        }

        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a BTService
                    setupBT();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, getString(R.string.bt_not_enabled));
                    Toast.makeText(this, R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }
}
