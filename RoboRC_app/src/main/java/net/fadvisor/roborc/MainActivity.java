/**
 * Created by Fahad Alduraibi on 12/4/14.
 * fadvisor.net
 */

package net.fadvisor.roborc;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.GridLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

    public static volatile Context myContext;

    // Message types sent from the BluetoothService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    // Key names received from the BluetoothService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    public static MySeekBar sbL;
    public static MySeekBar sbR;
    private static ToggleButton btConnect;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_WRITE:
                    // byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    // String writeMessage = new String(writeBuf);
                    break;
                case MESSAGE_READ:
                    // byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    // String readMessage = new String(readBuf, 0, msg.arg1);
                    break;
                case MESSAGE_DEVICE_NAME:
                    // show the connected device's name
                    String mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(myContext, "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();

                    // Turn connect button on
                    btConnect.setEnabled(true);
                    btConnect.setChecked(true);

                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(myContext, msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                    btConnect.setEnabled(true);
                    break;
            }
        }
    };
    // String buffer for outgoing messages
//    private StringBuffer mOutStringBuffer; // I will use it later to send info about battery power (Low battery?)
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the bluetooth services
    private BluetoothService btService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myContext = getApplicationContext();
        setContentView(R.layout.activity_main);
        final View rl_L = findViewById(R.id.rl_L);
        final View rl_R = findViewById(R.id.rl_R);

        sbL = (MySeekBar) findViewById(R.id.sbL);
        sbR = (MySeekBar) findViewById(R.id.sbR);

        btConnect = (ToggleButton) findViewById(R.id.btConnect);

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, getString(R.string.btNotAvailable), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        GridLayout mainGrid = (GridLayout)findViewById(R.id.mainGrid);
        mainGrid.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                GridLayout mainGrid = (GridLayout) findViewById(R.id.mainGrid);
                resizeMainGrid(mainGrid);

                if (android.os.Build.VERSION.SDK_INT >= 16)
                    mainGrid.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                else
                    mainGrid.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

        // Bring seekbars to front to make sure they won't be covered by other objects in devices with small screen
        sbL.bringToFront();
        sbR.bringToFront();

        mContentView = this.getWindow().getDecorView();
    }

    public void resizeMainGrid (GridLayout mainGrid) {
        LinearLayout centerLL;

        int centerWidth = (int) (mainGrid.getWidth() - mainGrid.getChildAt(1).getWidth() - mainGrid.getChildAt(2).getWidth());
        int centerHeight = (int) (mainGrid.getHeight() - mainGrid.getChildAt(1).getHeight());

        // The first element in the xml file must be the central one with the connect button
        centerLL = (LinearLayout) mainGrid.getChildAt(0);

        centerLL.setMinimumWidth(centerWidth);
        centerLL.setMinimumHeight(centerHeight);
    }

    public static void ResetSeekBar(final View v) {
        final MySeekBar tempsb;
        if (v.getId() == R.id.sbL) {
            tempsb = sbL;
        } else {
            tempsb = sbR;
        }

        ValueAnimator anim = ValueAnimator.ofInt(tempsb.getProgress(), 50);
        anim.setDuration(100);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animProgress = (Integer) animation.getAnimatedValue();
                tempsb.setProgress(animProgress);
            }
        });
        anim.start();
    }

    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
//        if (!mBluetoothAdapter.isEnabled()) {
//            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
//        } else {
//            if (btService == null) btService = new BluetoothService(this, mHandler);
//        }
    }

    public void btConnectClick(View v) {
        if (btConnect.isChecked()) {
            // Launch the DeviceListActivity to see devices and do scan
            Intent serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            btConnect.setChecked(false);
            btConnect.setEnabled(false);
        } else {
            btService.stop();
        }
    }

    public void btnRotate_Click(View v) {
        final MySeekBar tempsb;
        if (v.getId() == R.id.btnRotateL) {
            tempsb = sbL;
        } else {
            tempsb = sbR;
        }

        final float fOldRotation = tempsb.getRotation();
        float fNewRotation = 360;
        if (fOldRotation == 360)
            fNewRotation = 270;

        ValueAnimator anim = ValueAnimator.ofFloat(fOldRotation, fNewRotation);
        anim.setDuration(200);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animProgress = (float) animation.getAnimatedValue();
                tempsb.setRotation(animProgress);
            }
        });
        anim.start();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BLuetoothDevice object
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    // Attempt to connect to the device
                    btService.connect(device);
                } else {
                    btConnect.setChecked(false);
                    btConnect.setEnabled(true);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    btService = new BluetoothService(this, mHandler);
                } else {
                    // User did not enable Bluetooth or an error occured
                    Toast.makeText(this, R.string.btNotEnabledFinish, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedFullscreenUI(300);

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (btService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (btService.getState() == BluetoothService.STATE_CONNECTED) {
                btConnect.setChecked(true);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth services
        if (btService != null) btService.stop();
    }

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedFullscreenUI(int delayMillis) {
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, delayMillis);
    }
}
