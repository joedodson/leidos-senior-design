package com.leidossd.dronecontrollerapp.missions;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import java.util.Random;

public class MissionRunnerService extends IntentService implements Task.StatusUpdateListener {
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

    // Needed to allow components to create persistent binding to service to access its methods
    @Override
    public IBinder onBind(Intent intent) {
        localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        return missionRunnerBinder;
    }

    // What actually gets called when service.startForeground() is called
    protected void onHandleIntent(Intent missionIntent) {
        mission = missionIntent.getParcelableExtra(MISSION_BUNDLE_EXTRA_NAME);
//        mission.setMissionUpdateCallback(new Mission.MissionUpdateCallback() {
//            @Override
//            public void onMissionStart(String missionStartResult) {
//                sendServiceStatusUpdate(ServiceStatusUpdate.MISSION_START, missionStartResult);
//            }

//            @Override
//            public void onMissionFinish(String missionFinishResult) {
//                sendServiceStatusUpdate(ServiceStatusUpdate.MISSION_FINISH, missionFinishResult);
//            }

//            @Override
//            public void onMissionError(String missionErrorMessage) {
//                sendServiceStatusUpdate(ServiceStatusUpdate.MISSION_ERROR, missionErrorMessage);
//            }
//        });

        mission.start();
    }

    public Mission getCurrentMission() {
        return mission;
    }

    // Sends a local broadcast whenever it gets something from a mission callback
    private void sendServiceStatusUpdate(ServiceStatusUpdate serviceStatusUpdate, String message) {
        Intent intent = new Intent(serviceStatusUpdate.action);
        intent.putExtra(ServiceStatusUpdate.getResultKey(), message);
        localBroadcastManager.sendBroadcast(intent);
    }

    // Simple enum for readability with intents and intent parameters
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


    @Override
    public void statusUpdate(Task.TaskState status, String message){
        switch(status){
            case RUNNING:
                sendServiceStatusUpdate(ServiceStatusUpdate.MISSION_START, message);
                break;
            case FAILED:
                sendServiceStatusUpdate(ServiceStatusUpdate.MISSION_ERROR, message);
                break;
            case COMPLETED:
                sendServiceStatusUpdate(ServiceStatusUpdate.MISSION_FINISH, message);
                break;
            default:
                break;
        }
    }
}
