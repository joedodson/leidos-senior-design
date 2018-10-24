package com.leidossd.dronecontrollerapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import dji.common.error.DJIError;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.sdkmanager.DJISDKManager;

public class DroneConnectionActivity extends Activity {
    private static final String TAG = DroneConnectionActivity.class.getName();

    DJISDKManager.SDKManagerCallback DJISDKManagerCallback;
    TextView productConnectionStatus;
    ProgressBar productConnectProgress;

    // separate runnables for progress bar and status update
    Handler connectFailedHandler;
    Runnable connectFailedRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drone_connection);

        // get current connection status and set UI text
        BaseProduct drone = DJISDKManager.getInstance().getProduct();
        int statusId = (drone != null && drone.isConnected()) ?
                R.string.activity_droneconnection_statusConnected :
                R.string.activity_droneconnection_statusDisconnected;

        productConnectProgress = findViewById(R.id.connectProgressBar);
        productConnectionStatus = findViewById(R.id.status_text);
        productConnectionStatus.setText(statusId);

        // handler and runnable to show error message and hide progress bar after timeout
        connectFailedHandler = new Handler();
        connectFailedRunnable = new Runnable() {
            @Override
            public void run() {
                updateUI(R.string.activity_droneconnection_connectError, View.GONE);
            }
        };

        DJISDKManagerCallback = new DJISDKManager.SDKManagerCallback() {

            @Override
            public void onRegister(final DJIError error) {
            }

            // remove callback to prevent failed message, set correct status, and log result
            @Override
            public void onProductDisconnect() {
                updateUI(R.string.activity_droneconnection_statusDisconnected, View.GONE);

                showToast("Product Disconnected");
                Log.d(TAG, "onProductDisconnect");
            }

            @Override
            public void onProductConnect(BaseProduct baseProduct) {
                updateUI(R.string.activity_droneconnection_statusConnected, View.GONE);

                showToast("Product Connected");
                Log.d(TAG, String.format("onProductConnect newProduct:%s", baseProduct));
            }

            @Override
            public void onComponentChange(BaseProduct.ComponentKey componentKey, BaseComponent oldComponent,
                                          BaseComponent newComponent) {
                updateUI(R.string.activity_droneconnection_statusChanged, View.GONE);

                showToast("Component Changed");
                if (newComponent != null) {
                    newComponent.setComponentListener(new BaseComponent.ComponentListener() {

                        @Override
                        public void onConnectivityChange(boolean isConnected) {
                            Log.d("TAG", "onComponentConnectivityChanged: " + isConnected);
                        }
                    });
                }

                Log.d(TAG,
                        String.format("onComponentChange key:%s, oldComponent:%s, newComponent:%s",
                                componentKey,
                                oldComponent,
                                newComponent));
            }
        };
    }

    public void startConnection(View view) {
        DJISDKManager.getInstance().startConnectionToProduct();
        showToast("Starting connection to product...");

        updateUI(R.string.activity_droneconnection_connecting, View.VISIBLE);

        // show failed message after 5 seconds
        connectFailedHandler.postDelayed(connectFailedRunnable, 5000);
    }

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
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
