package com.leidossd.dronecontrollerapp;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.leidossd.dronecontrollerapp.missions.runner.MissionRunner;
import com.secneo.sdk.Helper;

/**
 * Still not entirely sure why, but this is needed to handle runtime loading of classes in DJI Apps.
 * This class is used entry point in the manifest.
 */

public class BootstrapApplication extends Application {

    private static MainApplication mainApplication;
    private static MissionRunner missionRunnerInstance = null;

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

        missionRunnerInstance = MissionRunner.getInstance();
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

    public static MissionRunner getMissionRunnerInstance() {
        return missionRunnerInstance;
    }
}