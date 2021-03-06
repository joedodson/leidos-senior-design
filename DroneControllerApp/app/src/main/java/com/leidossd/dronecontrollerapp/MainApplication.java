package com.leidossd.dronecontrollerapp;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.leidossd.djiwrapper.CoordinateFlightControl;
import com.leidossd.djiwrapper.FlightControllerWrapper;
import com.leidossd.utils.DroneConnectionStatus;

import dji.common.error.DJIError;
import dji.common.error.DJISDKError;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.camera.Camera;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKManager;

import static com.leidossd.utils.DroneConnectionStatus.DRONE_CONNECTED;
import static com.leidossd.utils.DroneConnectionStatus.DRONE_DISCONNECTED;
import static com.leidossd.utils.IntentAction.CONNECTION_CHANGE;
import static com.leidossd.utils.IntentAction.REGISTRATION_RESULT;

/**
 * This class iw where the actual application logic goes. The Application class is a container
 * for all other activities and services and can be referenced by them. Should not be used to share
 * mutable data between activities.
 *
 * @see <a href=https://github.com/codepath/android_guides/wiki/Understanding-the-Android-Application-Class />
 */

public class MainApplication extends Application {
    private static final String TAG = MainApplication.class.getName();

    static LocalBroadcastManager localBroadcastManager;

    private static BootstrapApplication baseApplication;

    private static Aircraft droneInstance;
    private static Camera cameraInstance;
    private static boolean droneConnected;

    static DJISDKManager.SDKManagerCallback djiSdkManagerCallback;

    public MainApplication() {
    }

    public void setContext(BootstrapApplication application) {
        baseApplication = application;
    }

    @Override
    public Context getApplicationContext() {
        return baseApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        droneConnected = false;

        localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());

        djiSdkManagerCallback = new DJISDKManager.SDKManagerCallback() {
            @Override
            public void onRegister(DJIError result) {
                boolean registrationResult = result == DJISDKError.REGISTRATION_SUCCESS;

                Intent registrationIntent = new Intent(REGISTRATION_RESULT.getActionString());
                registrationIntent.putExtra(REGISTRATION_RESULT.getResultKey(), registrationResult);

                localBroadcastManager.sendBroadcast(registrationIntent);
                DJISDKManager.getInstance().startConnectionToProduct();

                Log.d(TAG, "DJI SDK Registered with result: " + String.valueOf(registrationResult));
            }

            @Override
            public void onProductDisconnect() {
                if (getDroneInstance() != null && !getDroneInstance().isConnected()) {
                    droneConnected = false;
                    broadcastConnectionChange(DRONE_DISCONNECTED);
                    Log.d(TAG, "Drone Disconnected");
                }
            }

            @Override
            public void onProductConnect(BaseProduct baseProduct) {
                if (getDroneInstance() != null && getDroneInstance().isConnected()) {
                    droneConnected = true;
                    FlightControllerWrapper.getInstance().setFlightMode(CoordinateFlightControl.FlightMode.ABSOLUTE);
                    broadcastConnectionChange(DRONE_CONNECTED);
                    Log.d(TAG, "Product Connected");
                }
            }

            @Override
            public void onComponentChange(BaseProduct.ComponentKey componentKey, BaseComponent oldComponent, BaseComponent newComponent) {
                if (getDroneInstance() != null && getDroneInstance().isConnected() != droneConnected) {
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

        DJISDKManager.getInstance().registerApp(getApplicationContext(), djiSdkManagerCallback);
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
        } else if (cameraInstance == null) {
            cameraInstance = getDroneInstance().getCamera();
        }

        return cameraInstance;
    }

    public static void showToast(String msg) {
        Toast.makeText(baseApplication, msg, Toast.LENGTH_SHORT).show();
    }

    public static void registerSdk() {
        DJISDKManager.getInstance().registerApp(baseApplication, djiSdkManagerCallback);
    }
}