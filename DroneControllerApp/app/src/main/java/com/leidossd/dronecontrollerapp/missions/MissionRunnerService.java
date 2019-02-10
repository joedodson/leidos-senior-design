package com.leidossd.dronecontrollerapp.missions;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import java.util.Random;

public class MissionRunnerService extends IntentService {

    private final MissionRunnerBinder missionRunnerBinder = new MissionRunnerBinder();
    private static final String MISSION_BUNDLE_EXTRA_NAME = "MISSION_EXTRA";
    public static Mission mission;
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

    @Override
    public IBinder onBind(Intent intent) {
        return missionRunnerBinder;
    }

    protected void onHandleIntent(Intent missionIntent) {
        // Normally we would do some work here, like download a file.
        // For our sample, we just sleep for 5 seconds.
//        Log.w("mission runner","waiting for debugger");
//        android.os.Debug.waitForDebugger();
//        Log.w("mission runner", "done waiting");
        Toast.makeText(getApplicationContext(), "handling intent", Toast.LENGTH_LONG).show();
        mission = missionIntent.getParcelableExtra(MISSION_BUNDLE_EXTRA_NAME);
        mission.start();
    }

    public Mission getCurrentMission() {
        return mission;
    }
}
