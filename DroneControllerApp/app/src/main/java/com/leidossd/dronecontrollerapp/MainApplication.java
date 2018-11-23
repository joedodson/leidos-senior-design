package com.leidossd.dronecontrollerapp;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import dji.common.error.DJIError;
import dji.common.error.DJISDKError;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.sdkmanager.DJISDKManager;

import utils.DroneConnectionStatus;

import static utils.IntentAction.*;
import static utils.DroneConnectionStatus.*;

/**
 * This class iw where the actual application logic goes. The Application class is a container
 * for all other activities and services and can be referenced by them. Should not be used to share
 * mutable data between activities.
 * @see <a href=https://github.com/codepath/android_guides/wiki/Understanding-the-Android-Application-Class />
 */

public class MainApplication extends Application {
    private static final String TAG = MainApplication.class.getName();

    private Handler handler = new Handler(Looper.getMainLooper());
    private DJISDKManager.SDKManagerCallback DJISDKManagerCallback;
    LocalBroadcastManager localBroadcastManager;

    private Application baseApplication;


    public MainApplication() { }

    public void setContext(Application application) {
        this.baseApplication = application;
    }

    @Override
    public Context getApplicationContext() { return baseApplication; }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(Looper.getMainLooper());

        localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());

        DJISDKManagerCallback = new DJISDKManager.SDKManagerCallback() {
            @Override
            public void onRegister(DJIError result) {
                boolean registrationResult = result == DJISDKError.REGISTRATION_SUCCESS;
                showToast("registration completed with result: " + registrationResult);

                Intent registrationIntent = new Intent(REGISTRATION_RESULT.getActionString());
                registrationIntent.putExtra(REGISTRATION_RESULT.getResultKey(), registrationResult);

                localBroadcastManager.sendBroadcast(registrationIntent);
                DJISDKManager.getInstance().startConnectionToProduct();
            }

            @Override
            public void onProductDisconnect() {
                broadcastConnectionChange(DRONE_DISCONNECTED);
                showToast(TAG + " disconnect");
                Log.d(TAG, "Product Disconnected");
            }

            @Override
            public void onProductConnect(BaseProduct baseProduct) {
                broadcastConnectionChange(DRONE_CONNECTED);
                showToast(TAG + " connect");
                Log.d(TAG, "Product Connected");
            }

            @Override
            public void onComponentChange(BaseProduct.ComponentKey componentKey, BaseComponent baseComponent, BaseComponent baseComponent1) {
                showToast(TAG + " change");
                Log.d(TAG, "Component Changed");
            }
        };

        DJISDKManager.getInstance().registerApp(getApplicationContext(), DJISDKManagerCallback);
    }

    private void broadcastConnectionChange(DroneConnectionStatus droneStatus) {
        Intent connectionChangeIntent = new Intent(CONNECTION_CHANGE.getActionString());
        connectionChangeIntent.putExtra(CONNECTION_CHANGE.getResultKey(), droneStatus.getStatus());

        localBroadcastManager.sendBroadcast(connectionChangeIntent);
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}