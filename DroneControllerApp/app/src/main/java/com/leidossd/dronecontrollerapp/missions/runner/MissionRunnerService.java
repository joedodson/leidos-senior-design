package com.leidossd.dronecontrollerapp.missions.runner;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;

import com.leidossd.dronecontrollerapp.missions.Mission;

public class MissionRunnerService extends IntentService {

    private final MissionRunnerBinder missionRunnerBinder = new MissionRunnerBinder();
    private static final String MISSION_BUNDLE_EXTRA_NAME = "MISSION_EXTRA";
    private static Mission mission;

    public MissionRunnerService() {
        super(MissionRunnerService.class.getName());
    }

     class MissionRunnerBinder extends Binder {
        MissionRunnerService getService() {
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
        mission = missionIntent.getParcelableExtra(MISSION_BUNDLE_EXTRA_NAME);
        mission.start();
    }

    public Mission getCurrentMission() {
        return mission;
    }
}
