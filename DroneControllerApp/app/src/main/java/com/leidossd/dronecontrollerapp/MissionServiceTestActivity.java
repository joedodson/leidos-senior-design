package com.leidossd.dronecontrollerapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.leidossd.dronecontrollerapp.missions.Mission;
import com.leidossd.dronecontrollerapp.missions.SpecificMission;
import com.leidossd.dronecontrollerapp.missions.MissionRunner;

public class MissionServiceTestActivity extends AppCompatActivity {

    MissionRunner missionRunner;
    MissionRunner missionRunner2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_service_test);

        missionRunner = new MissionRunner(this);
        missionRunner2 = new MissionRunner(this);
    }

    public void startService(View view) {
        missionRunner.startMission(this, new SpecificMission("SpecificMission", new Mission.MissionUpdateCallback() {
            @Override
            public void onMissionStart(String missionStartResult) {
                MainApplication.showToast(missionStartResult);
            }

            @Override
            public void onMissionFinish(String missionFinishResult) {
                MainApplication.showToast(missionFinishResult);
            }

            @Override
            public void onMissionError(String missionErrorMessage) {
                MainApplication.showToast(missionErrorMessage);
            }
        }));

        missionRunner2.startMission(this, new SpecificMission("NewSpecificMission", new Mission.MissionUpdateCallback() {
            @Override
            public void onMissionStart(String missionStartResult) {
                MainApplication.showToast("m2 "+missionStartResult);
            }

            @Override
            public void onMissionFinish(String missionFinishResult) {
                MainApplication.showToast("m2 "+missionFinishResult);
            }

            @Override
            public void onMissionError(String missionErrorMessage) {
                MainApplication.showToast("m2 "+missionErrorMessage);
            }
        }));
    }
}
