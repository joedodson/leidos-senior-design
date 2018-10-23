package com.leidossd.dronecontrollerapp;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

/**
 * This class iw where the actual application logic goes. The Application class is a container
 * for all other activities and services and can be referenced by them. Should not be used to share
 * mutable data between activities.
 * @see <a href=https://github.com/codepath/android_guides/wiki/Understanding-the-Android-Application-Class />
 */

public class MainApplication extends Application {
    private static final String TAG = MainApplication.class.getName();
    private static Application self;

    private static Handler handler = new Handler(Looper.getMainLooper());

    public MainApplication(Application application) { }

    @Override
    public void onCreate() {
        super.onCreate();
        self = this;
    }
}