package com.leidossd.dronecontrollerapp;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import dji.common.error.DJIError;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.sdkmanager.DJISDKManager;

/**
 * This class iw where the actual application logic goes. The Application class is a container
 * for all other activities and services and can be referenced by them. Should not be used to share
 * mutable data between activities.
 * @see <a href=https://github.com/codepath/android_guides/wiki/Understanding-the-Android-Application-Class />
 */

public class MainApplication extends Application {
    private static final String TAG = MainApplication.class.getName();

    public static final String FLAG_CONNECTION_CHANGE = "drone_connection_change";

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable connectionUpdateRunnable;
    private DJISDKManager.SDKManagerCallback DJISDKManagerCallback;

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

        connectionUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                getApplicationContext().sendBroadcast(new Intent(FLAG_CONNECTION_CHANGE));
            }
        };

        DJISDKManagerCallback = new DJISDKManager.SDKManagerCallback() {
            @Override
            public void onRegister(DJIError djiError) { }

            @Override
            public void onProductDisconnect() {
                handler.postDelayed(connectionUpdateRunnable, 250);
                Log.d(TAG, "Product Disconnected");
            }

            @Override
            public void onProductConnect(BaseProduct baseProduct) {
                handler.postDelayed(connectionUpdateRunnable, 250);
                Log.d(TAG, "Product Connected");
            }

            @Override
            public void onComponentChange(BaseProduct.ComponentKey componentKey, BaseComponent baseComponent, BaseComponent baseComponent1) {
                handler.postDelayed(connectionUpdateRunnable, 250);
                Log.d(TAG, "Component Changed");
            }
        };
    }
}