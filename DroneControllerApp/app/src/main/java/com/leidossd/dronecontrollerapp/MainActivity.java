package com.leidossd.dronecontrollerapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;

import com.leidossd.dronecontrollerapp.missions.MissionRunner;

import static com.leidossd.utils.DroneConnectionStatus.DRONE_CONNECTED;
import static com.leidossd.utils.IntentAction.CONNECTION_CHANGE;

public class MainActivity extends MenuActivity {

    private static final String TAG = MainActivity.class.getName();
    MainApplication app = (MainApplication) getApplication();

    LiveVideoFragment liveVideoFragment;
    AlertDialog droneNotConnectedDialog;

    private static MissionRunner missionRunner;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        setContentView(R.layout.activity_main);

        super.onCreate(savedInstanceState);

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

        liveVideoFragment = new LiveVideoFragment();

        droneNotConnectedDialog = new AlertDialog.Builder(this)
                .setTitle("No Aircraft Connected")
                .setMessage("This feature is not available without a connected aircraft.")
                .setPositiveButton("OK", null)
                .create();
    }


    // remove callback to prevent failed message, set correct status, and log result
    private void toggleLiveVideo(String droneStatus) {
        if(droneStatus.equals(DRONE_CONNECTED.toString())) {
            startLiveVideo();
        } else {
            stopLiveVideo();
        }
    }

    private void startLiveVideo() {
        if(!liveVideoFragment.isAdded()) {
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