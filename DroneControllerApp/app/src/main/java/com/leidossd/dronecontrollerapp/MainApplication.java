package com.leidossd.dronecontrollerapp;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import dji.common.error.DJIError;
import dji.common.error.DJISDKError;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.camera.Camera;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKManager;

import com.leidossd.utils.DroneConnectionStatus;

import static com.leidossd.utils.IntentAction.*;
import static com.leidossd.utils.DroneConnectionStatus.*;

/**
 * This class iw where the actual application logic goes. The Application class is a container
 * for all other activities and services and can be referenced by them. Should not be used to share
 * mutable data between activities.
 *
 * @see <a href=https://github.com/codepath/android_guides/wiki/Understanding-the-Android-Application-Class />
 */

public class MainApplication extends Application {
    private static final String TAG = MainApplication.class.getName();

    private Handler handler = new Handler(Looper.getMainLooper());
    private DJISDKManager.SDKManagerCallback DJISDKManagerCallback;
    LocalBroadcastManager localBroadcastManager;

    private Application baseApplication;

    private static Aircraft droneInstance;
    private static Camera cameraInstance;
    private static boolean droneConnected;

    public MainApplication() {
    }

    public void setContext(Application application) {
        this.baseApplication = application;
    }

    @Override
    public Context getApplicationContext() {
        return baseApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(Looper.getMainLooper());
        droneConnected = false;

        localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());

        DJISDKManagerCallback = new DJISDKManager.SDKManagerCallback() {
            @Override
            public void onRegister(DJIError result) {
                boolean registrationResult = result == DJISDKError.REGISTRATION_SUCCESS;

                Intent registrationIntent = new Intent(REGISTRATION_RESULT.getActionString());
                registrationIntent.putExtra(REGISTRATION_RESULT.getResultKey(), registrationResult);

                localBroadcastManager.sendBroadcast(registrationIntent);
                DJISDKManager.getInstance().startConnectionToProduct();
            }

            @Override
            public void onProductDisconnect() {
                if(getDroneInstance() != null && !getDroneInstance().isConnected()) {
                    droneConnected = false;
                    broadcastConnectionChange(DRONE_DISCONNECTED);
                    Log.d(TAG, "Drone Disconnected");
                }
            }

            @Override
            public void onProductConnect(BaseProduct baseProduct) {
                if(getDroneInstance() != null && getDroneInstance().isConnected()) {
                    droneConnected = true;
                    broadcastConnectionChange(DRONE_CONNECTED);
                    Log.d(TAG, "Product Connected");
                }
            }

            @Override
            public void onComponentChange(BaseProduct.ComponentKey componentKey, BaseComponent oldComponent, BaseComponent newComponent) {
                if(getDroneInstance() != null && getDroneInstance().isConnected() != droneConnected) {
                    DroneConnectionStatus status = getDroneInstance().isConnected() ? DRONE_CONNECTED : DRONE_DISCONNECTED;
                    broadcastConnectionChange(status);
                }
                Log.d(TAG,
                        String.format("Component Changed key:%s, oldComponent:%s, newComponent:%s",
                                componentKey,
                                oldComponent,
                                newComponent));
            }
        };

        DJISDKManager.getInstance().registerApp(getApplicationContext(), DJISDKManagerCallback);
    }

    private void broadcastConnectionChange(DroneConnectionStatus droneStatus) {
        Intent connectionChangeIntent = new Intent(CONNECTION_CHANGE.getActionString());
        connectionChangeIntent.putExtra(CONNECTION_CHANGE.getResultKey(), droneStatus.toString());

        localBroadcastManager.sendBroadcast(connectionChangeIntent);
    }

    /**
     * This function is used to get the instance of DJIBaseProduct.
     * If no product is connected, it returns null.
     */
    public static synchronized Aircraft getDroneInstance() {
        if (droneInstance == null) {
            try {
                droneInstance = (Aircraft) DJISDKManager.getInstance().getProduct();
            } catch (ClassCastException e) {
                Log.w(TAG, "Attempt to cast base product to aircraft failed on: "
                        + DJISDKManager.getInstance().getProduct().toString());
            }
        }

        return droneInstance;
    }

    public static synchronized Camera getCameraInstance() {
        if (getDroneInstance() == null) {
            cameraInstance = null;
        } else if(cameraInstance == null) {
            cameraInstance = getDroneInstance().getCamera();
        }

        return cameraInstance;
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}