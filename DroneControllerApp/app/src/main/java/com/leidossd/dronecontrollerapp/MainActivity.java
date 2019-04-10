package com.leidossd.dronecontrollerapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.leidossd.djiwrapper.FlightControllerWrapper;

import static com.leidossd.utils.DroneConnectionStatus.DRONE_CONNECTED;
import static com.leidossd.utils.IntentAction.CONNECTION_CHANGE;

public class MainActivity extends MenuActivity {
    private static final String TAG = MainActivity.class.getName();

    MainApplication app = (MainApplication) getApplication();

    LiveVideoFragment liveVideoFragment;
    AlertDialog droneNotConnectedDialog;
    Handler handler;

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
    }

    // remove callback to prevent failed message, set correct status, and log result
    private void toggleLiveVideo(String droneStatus) {
        if (droneStatus.equals(DRONE_CONNECTED.toString())) {
            startLiveVideo();
        } else {
            stopLiveVideo();
        }
    }

    private void startLiveVideo() {
        if (!liveVideoFragment.isAdded()) {
            fragmentManager.beginTransaction()
                    .add(R.id.live_video_fragment_container, liveVideoFragment)
                    .commitAllowingStateLoss();
        }
    }

    private void stopLiveVideo() {
        if (liveVideoFragment.isAdded()) {
            fragmentManager.beginTransaction()
                    .remove(liveVideoFragment)
                    .commitAllowingStateLoss();
        }
    }

    // TODO: remove before release
    public void land(View view) {
        try {
            FlightControllerWrapper.getInstance().startLanding(djiError -> {
                if (djiError != null) {
                    runOnUiThread(() -> MainApplication.showToast("Couldn't not land - check logs"));
                    findViewById(R.id.btn_land).setEnabled(false);
                    Log.e(TAG, "Could not land: " + djiError.getDescription());
                }
            });
        } catch (Exception e) {
            MainApplication.showToast("Could not land - Check logs");
            findViewById(R.id.btn_land).setEnabled(false);
            Log.e(TAG, "Could not land: " + e.getMessage());
        }
    }

    // TODO: remove before release
    public void confirmLand(View view) {
        try {
            FlightControllerWrapper.getInstance().confirmLanding(djiError -> {
                if (djiError == null)
                    runOnUiThread(() -> MainApplication.showToast("Unable to land - DJIError"));
            });
        } catch (Exception e) {
            MainApplication.showToast("Could not confirm land - Check logs");
            Log.e(TAG, "Could not confirm land: " + e.getMessage());
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
        if (MainApplication.getDroneInstance() != null && MainApplication.getDroneInstance().isConnected()) {
            startLiveVideo();
        }
    }
}