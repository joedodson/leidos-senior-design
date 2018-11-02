package com.leidossd.dronecontrollerapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import dji.sdk.base.BaseProduct;
import dji.sdk.sdkmanager.DJISDKManager;

public class DroneConnectionActivity extends Activity {
    private static final String TAG = DroneConnectionActivity.class.getName();

    private static final long CONNECT_TIMEOUT_MS = 10000;

    private TextView productConnectionStatus;
    private ProgressBar productConnectProgress;

    // handler and runnable to show error message and hide progress bar after timeout
    private Handler connectFailedHandler;
    private Runnable connectFailedRunnable = new Runnable() {
        @Override
        public void run() {
            updateUI(R.string.activity_droneconnection_connectError, View.GONE);
        }
    };

    // receiver to wait for 'MainApplication' to notify connection status change
    private BroadcastReceiver connectionChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(getConnectionStatusStringId(), View.GONE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drone_connection);

        registerReceiver(connectionChangeReceiver, new IntentFilter(MainApplication.FLAG_CONNECTION_CHANGE));

        productConnectProgress = findViewById(R.id.connectProgressBar);
        productConnectionStatus = findViewById(R.id.status_text);
        productConnectionStatus.setText(getConnectionStatusStringId());

        connectFailedHandler = new Handler();
    }

    public void startConnection(View view) {
        DJISDKManager.getInstance().startConnectionToProduct();
        showToast("Starting connection to product...");

        updateUI(R.string.activity_droneconnection_connecting, View.VISIBLE);

        // show failed message after 5 seconds
        connectFailedHandler.postDelayed(connectFailedRunnable, CONNECT_TIMEOUT_MS);
    }


    // remove callback to prevent failed message, set correct status, and log result
    private void updateUI(int stringId, int visibilityId) {
        connectFailedHandler.removeCallbacks(connectFailedRunnable);

        int colorId;
        switch (stringId) {
            case R.string.activity_droneconnection_statusConnected:
                colorId = R.color.text_success;
                break;
            case R.string.activity_droneconnection_connectError:
                colorId = R.color.text_error;
                break;
            default:
                colorId = R.color.text_dark;
        }

        productConnectionStatus.setTextColor(getColor(colorId));
        productConnectionStatus.setText(getString(stringId));
        productConnectProgress.setVisibility(visibilityId);

        Log.d(TAG, String.format("Update UI: %s", getString(stringId)));
    }

    int getConnectionStatusStringId() {
        BaseProduct drone = DJISDKManager.getInstance().getProduct();
        return (drone != null && drone.isConnected()) ?
                R.string.activity_droneconnection_statusConnected :
                R.string.activity_droneconnection_statusDisconnected;
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}