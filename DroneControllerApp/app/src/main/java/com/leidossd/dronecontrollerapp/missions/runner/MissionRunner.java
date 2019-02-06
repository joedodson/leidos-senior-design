package com.leidossd.dronecontrollerapp.missions.runner;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;

import com.leidossd.dronecontrollerapp.missions.Mission;

public class MissionRunner {

    private MissionRunnerService missionRunnerService;
    private boolean missionRunnerServiceIsBound = false;


    private static MissionRunner missionRunnerInstance;

    private static final String MISSION_BUNDLE_EXTRA_NAME = "MISSION_EXTRA";

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection missionRunnerServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MissionRunnerService.MissionRunnerBinder binder = (MissionRunnerService.MissionRunnerBinder) service;
            missionRunnerService = binder.getService();
            missionRunnerServiceIsBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            missionRunnerServiceIsBound = false;
        }
    };

    private MissionRunner() { }

    public static MissionRunner getInstance() {
        if (missionRunnerInstance == null) {
            missionRunnerInstance = new MissionRunner();
        }

        return missionRunnerInstance;
    }

    public void startMission(Context applicationContext, Mission mission) {
        Intent missionIntent = new Intent(applicationContext, MissionRunnerService.class);
        missionIntent.putExtra(MISSION_BUNDLE_EXTRA_NAME, mission);
        missionRunnerService.startForegroundService(missionIntent);
    }

    public String getTestData() {
        return missionRunnerService.getTestData();
    }


    private class MissionRunnerService extends IntentService {

        private final MissionRunnerBinder missionRunnerBinder = new MissionRunnerBinder();

        public MissionRunnerService() {
            super(MissionRunnerService.class.getName());
        }

        public class MissionRunnerBinder extends Binder {
            public MissionRunnerService getService() {
                return MissionRunnerService.this;
            }
        }

        @Override
        public IBinder onBind(Intent intent) {
            return missionRunnerBinder;
        }

        protected void onHandleIntent(Intent missionIntent) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            try {
                Mission mission = missionIntent.getParcelableExtra(MISSION_BUNDLE_EXTRA_NAME);
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // Restore interrupt status.
                Thread.currentThread().interrupt();
            }
        }

        public String getTestData() {
            return "Hello from " + this.getClass().getName();
        }
    }
}

