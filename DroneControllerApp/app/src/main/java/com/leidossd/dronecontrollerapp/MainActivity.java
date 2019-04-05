package com.leidossd.dronecontrollerapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentManager;

import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;

import com.leidossd.dronecontrollerapp.missions.MissionRunner;

import dji.common.battery.BatteryState;
import dji.sdk.sdkmanager.DJISDKManager;

import static com.leidossd.dronecontrollerapp.MainApplication.getDroneInstance;
import static com.leidossd.utils.DroneConnectionStatus.DRONE_CONNECTED;
import static com.leidossd.utils.IntentAction.CONNECTION_CHANGE;

public class MainActivity extends MenuActivity {

    private static final String TAG = MainActivity.class.getName();
    MainApplication app = (MainApplication) getApplication();

    LiveVideoFragment liveVideoFragment;
    AlertDialog droneNotConnectedDialog;
    Handler handler;


    private static MissionRunner missionRunner;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        setContentView(R.layout.activity_main);

        handler = new Handler();
        super.onCreate(savedInstanceState);

        liveVideoFragment = new LiveVideoFragment();

        // MainApplication sends local broadcast when connection status changes
        // receiver to wait for 'MainApplication' to notify connection status change
        BroadcastReceiver connectionChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent connectionChangeIntent) {
                String droneStatus = connectionChangeIntent.getStringExtra(CONNECTION_CHANGE.getResultKey());
                toggleLiveVideo(droneStatus);
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(connectionChangeReceiver, new IntentFilter(CONNECTION_CHANGE.getActionString()));

        droneNotConnectedDialog = new AlertDialog.Builder(this)
                .setTitle("No Aircraft Connected")
                .setMessage("This feature is not available without a connected aircraft.")
                .setPositiveButton("OK", null)
                .create();

        // add the menu fragment, but don't show it
        fragmentManager.beginTransaction()
                .add(R.id.menu_fragment_container, menuFragment)
                .hide(menuFragment)
                .commit();

        actionBar = findViewById(R.id.status_actionbar);
        gestureDetector = new GestureDetectorCompat(this, new GestureListener());

        configureActionBar();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_actionbar, menu);

        Handler handler = new Handler();

        handler.postDelayed(() -> {
            TextView status = findViewById(R.id.action_bar_status);
            if(MainApplication.getDroneInstance() != null && status != null) {
                status.setText(DRONE_CONNECTED.toString());
            }
        }, 500);
        return true;
    }

    // Handles click events of items in action bar.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bar_status:
                showToast("Status");
                DJISDKManager.getInstance().startConnectionToProduct();
                break;
            case R.id.action_bar_compass:
                if(MainApplication.getDroneInstance() == null || !MainApplication.getDroneInstance().isConnected())
                    droneNotConnectedDialog.show();
                else
                    startActivity(new Intent(this, CompassCalibrationActivity.class));
                break;
            case R.id.action_bar_battery:
                showToast("Battery");
                break;
            case R.id.action_bar_main_menu:
                hideActionBar();
                showMenu();
                break;
            default:
                return false;
        }
        return true;
    }

    // handles actions clicked in side menu
    public void onMenuSelect(MenuAction action) {
        switch (action) {
            case OPEN_MISSIONS:
                startActivity(new Intent(this, MissionActivity.class));
                break;
            case OPEN_DEVELOPER:
                // Start Brians Activity
                startActivity(new Intent(this, MissionServiceExampleActivity.class));
                break;
            case OPEN_SIMULATOR:
//                startActivity(new Intent(this, SimulatorActivity.class));
                break;
            case OPEN_SETTINGS:
//                showToast("Settings");
                break;
            case OPEN_COMPASS:
//                startActivity(new Intent(this, CompassActivity.class));
                break;
            case OPEN_GRID_VIEW:
                startActivity(new Intent(this, GridParentActivity.class));
                break;
            default:
                showActionBar();
        }
        showActionBar();
        hideMenu();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {

            if(event1.getY() < event2.getY()) { // swipe down
                showActionBar();
            } else if(event1.getY() > event2.getY()) { // swipe up
                hideActionBar();
            }
            return true;
        }
    }


    // remove callback to prevent failed message, set correct status, and log result
    private void toggleLiveVideo(String droneStatus) {
        if(droneStatus.equals(DRONE_CONNECTED.toString())) {
            status = status += " " + getDroneInstance().getModel().getDisplayName();

            handler.removeCallbacksAndMessages(null);

            Runnable updateBatteryRunnable = new Runnable() {
                @Override
                public void run() {
                    MainApplication.getDroneInstance().getBattery().setStateCallback(batteryState -> {

                        int batteryPercent = batteryState.getChargeRemainingInPercent();
                        MenuView.ItemView batteryText = findViewById(R.id.action_bar_battery);

                        if (batteryPercent > 0) {
                            //showToast(String.format("Updating battery: %s %s", String.valueOf(batteryPercent), "%"));
                            runOnUiThread(() -> batteryText.setTitle(String.format("Battery: %s %s", String.valueOf(batteryPercent), "%")));
                        } else {
                            runOnUiThread(() -> batteryText.setTitle("Battery: 100%"));
                        }
                    });
                    handler.postDelayed(this, 10000);
                }
            };

            handler.post(updateBatteryRunnable);

            startLiveVideo();
        } else {
            stopLiveVideo();
        }
    }

    private void startLiveVideo() {
        if(!liveVideoFragment.isAdded()) {
            stopLiveVideo();
            liveVideoFragment = new LiveVideoFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.live_video_fragment_container, liveVideoFragment)
                    .commitAllowingStateLoss();
        }
    }

    private void stopLiveVideo() {
        if(liveVideoFragment.isAdded()) {
            fragmentManager.beginTransaction()
                    .remove(liveVideoFragment)
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLiveVideo();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(MainApplication.getDroneInstance() != null && MainApplication.getDroneInstance().isConnected()) {
            startLiveVideo();
        }
    }
}