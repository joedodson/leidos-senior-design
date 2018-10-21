package com.leidossd.dronecontrollerapp;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;


public class MainApplication extends Application {
    private static final String TAG = MainApplication.class.getName();
    private static Application self;

    private static Handler handler;

    public MainApplication(Application application) { }

    @Override
    public void onCreate() {
        super.onCreate();
        self = this;
        handler = new Handler(Looper.getMainLooper());
    }

    public static void showToast(final String msg) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                android.widget.Toast.makeText(self, msg, android.widget.Toast.LENGTH_LONG).show();
            }
        });
    }
}