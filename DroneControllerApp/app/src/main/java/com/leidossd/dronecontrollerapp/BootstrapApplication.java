package com.leidossd.dronecontrollerapp;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

import com.secneo.sdk.Helper;

/**
 * Still not entirely sure why, but this is needed to handle runtime loading of classes in DJI Apps.
 * This class is used entry point in the manifest.
 */

public class BootstrapApplication extends Application {

    private static MainApplication mainApplication;

    @Override
    protected void attachBaseContext(Context paramContext) {
        super.attachBaseContext(paramContext);

        if (mainApplication != null) {
            return;
        }

        Helper.install(this);
        loadApplication();
    }

    public void loadApplication() {
        mainApplication = new MainApplication();
        mainApplication.setContext(this);
    }

    public MainApplication getMainApplication() {
        return mainApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mainApplication.onCreate();

        createNotificationChannel();
    }

    @Override
    public void onConfigurationChanged(Configuration paramConfiguration) {
        super.onConfigurationChanged(paramConfiguration);
        mainApplication.onConfigurationChanged(paramConfiguration);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mainApplication.onLowMemory();

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notification_channel_name);
            String description = getString(R.string.notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(getString(R.string.notification_channel_name), name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}