package com.leidossd.dronecontrollerapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.leidossd.dronecontrollerapp.missions.SpecificMission;
import com.leidossd.dronecontrollerapp.missions.MissionRunner;

public class MissionServiceTestActivity extends AppCompatActivity {

    MissionRunner missionRunner;

    private static final String CHANNEL_ID = "SOME_CHANNEL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_service_test);

        missionRunner = BootstrapApplication.getMissionRunnerInstance();
    }

    public void startService(View view) {
        missionRunner.startMission(getApplicationContext(), new SpecificMission( "SpecificMission"));
    }
}
