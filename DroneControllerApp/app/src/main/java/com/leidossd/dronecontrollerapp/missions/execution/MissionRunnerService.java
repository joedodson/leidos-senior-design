package com.leidossd.dronecontrollerapp.missions.execution;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.leidossd.dronecontrollerapp.missions.Mission;

import java.util.Random;

public class MissionRunnerService extends IntentService {
    private final MissionRunnerBinder missionRunnerBinder = new MissionRunnerBinder();
    private static final String MISSION_BUNDLE_EXTRA_NAME = "MISSION_EXTRA";
    Mission mission;
    private Random r = new Random();
    public int id = r.nextInt();


    public MissionRunnerService() {
        super(MissionRunnerService.class.getName());
    }

    class MissionRunnerBinder extends Binder {
        MissionRunnerService getService() {
            return MissionRunnerService.this;
        }
    }

    // Needed to allow components to create persistent binding to service to access its methods
    @Override
    public IBinder onBind(Intent intent) {
        return missionRunnerBinder;
    }

    // What actually gets called when service.startForeground() is called
    protected void onHandleIntent(Intent missionIntent) {
        mission = missionIntent.getParcelableExtra(MISSION_BUNDLE_EXTRA_NAME);

        mission.start();
    }

    Mission getCurrentMission() {
        return mission;
    }
}
