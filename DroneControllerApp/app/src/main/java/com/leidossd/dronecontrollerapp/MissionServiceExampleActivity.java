package com.leidossd.dronecontrollerapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.leidossd.dronecontrollerapp.missions.Mission;
import com.leidossd.dronecontrollerapp.missions.SpecificMission;
import com.leidossd.dronecontrollerapp.missions.MissionRunner;
import com.leidossd.dronecontrollerapp.missions.Task;

public class MissionServiceExampleActivity extends AppCompatActivity implements Task.StatusUpdateListener {

    MissionRunner missionRunner;
    TextView missionStatusTextView;
    SpecificMission mission;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_service_example);

        missionStatusTextView = findViewById(R.id.tv_activity_mission_service_example_status);

        mission = new SpecificMission("SpecificMission");//, new Mission.MissionUpdateCallback() {
//            @Override
//            public void onMissionStart(String missionStartResult) {
//                setMissionStatus(missionStartResult);
//            }
//
//            @Override
//            public void onMissionFinish(String missionFinishResult) {
//                setMissionStatus(missionFinishResult);
//            }
//
//            @Override
//            public void onMissionError(String missionErrorMessage) {
//                setMissionStatus(missionErrorMessage);
//            }
//        });
//        missionStatusTextView.setText(mission.getStatus());
        missionRunner = new MissionRunner(this, this);
    }

    public void startService(View view) {
        missionRunner.startMission(this, mission);
    }

    private void setMissionStatus(String status) {
        runOnUiThread(()->missionStatusTextView.setText(String.format("Status: %s", status)));
    }

    @Override
    public void statusUpdate(Task.TaskState status, String message){
        setMissionStatus(message);
    }
}
