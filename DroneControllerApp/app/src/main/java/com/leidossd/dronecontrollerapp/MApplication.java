package com.leidossd.dronecontrollerapp;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.support.multidex.MultiDex;
import com.secneo.sdk.Helper;

public class MApplication extends Application {

    private static MainApplication mainApplication;

    @Override
    protected void attachBaseContext(Context paramContext) {
        super.attachBaseContext(paramContext);

        MultiDex.install(this);
        if (mainApplication != null) {
            return;
        }
        Helper.install(this);
        loadApplication(paramContext);
    }

    public void loadApplication(Context paramContext) {
        if (mainApplication != null)
            return;
        mainApplication = new MainApplication(this);
    }

    public MainApplication getMainApplication() {
        return mainApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mainApplication.onCreate();

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
}