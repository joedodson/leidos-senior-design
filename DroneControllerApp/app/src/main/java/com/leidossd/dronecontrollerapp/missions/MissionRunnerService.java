package com.leidossd.dronecontrollerapp.missions;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import java.util.Random;

public class MissionRunnerService extends IntentService {
    private final static String TAG = MissionRunnerService.class.getName();

    private final MissionRunnerBinder missionRunnerBinder = new MissionRunnerBinder();
    private static final String MISSION_BUNDLE_EXTRA_NAME = "MISSION_EXTRA";
    public Mission mission;
    private Random r = new Random();
    public int id = r.nextInt();

    private LocalBroadcastManager localBroadcastManager;

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
        localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        return missionRunnerBinder;
    }

    protected void onHandleIntent(Intent missionIntent) {
        Toast.makeText(this, "Handling intent", Toast.LENGTH_LONG).show();

        mission = missionIntent.getParcelableExtra(MISSION_BUNDLE_EXTRA_NAME);
        mission.setMissionUpdateCallback(new Mission.MissionUpdateCallback() {
            @Override
            public void onMissionStart(String missionStartResult) {
                sendServiceStatusUpdate(ServiceStatusUpdate.MISSION_START, missionStartResult);
            }

            @Override
            public void onMissionFinish(String missionFinishResult) {
                sendServiceStatusUpdate(ServiceStatusUpdate.MISSION_FINISH, "from service " + missionFinishResult);
            }

            @Override
            public void onMissionError(String missionErrorMessage) {
                sendServiceStatusUpdate(ServiceStatusUpdate.MISSION_ERROR, missionErrorMessage);
            }
        });

        Toast.makeText(this, "Starting mission: " + mission.title, Toast.LENGTH_LONG).show();
        mission.start();
    }

    public Mission getCurrentMission() {
        return mission;
    }

    private void sendServiceStatusUpdate(ServiceStatusUpdate serviceStatusUpdate, String message) {
        Intent intent = new Intent(serviceStatusUpdate.action);
        intent.putExtra(ServiceStatusUpdate.getResultKey(), message);
        localBroadcastManager.sendBroadcast(intent);
    }

    protected enum ServiceStatusUpdate {
        MISSION_START("Mission start"),
        MISSION_FINISH("Mission finish"),
        MISSION_ERROR("Mission error");

        String action;
        final static String resultKey = "status_update_key";

        ServiceStatusUpdate(String action) {
            this.action = action;
        }

        public static String getResultKey() {
            return resultKey;
        }
    }
}
