package com.leidossd.dronecontrollerapp.missions.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.leidossd.dronecontrollerapp.R;
import com.leidossd.dronecontrollerapp.missions.Mission;
import com.leidossd.dronecontrollerapp.missions.MissionRunner;
import com.leidossd.dronecontrollerapp.missions.Task;

import static com.leidossd.dronecontrollerapp.MainApplication.showToast;

public class MissionStatusActivity extends AppCompatActivity implements Task.StatusUpdateListener {
    private TextView missionStatus;
    private MissionRunner missionRunner;
    private Handler missionBindHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_status);

        missionStatus = findViewById(R.id.mission_status);
        missionRunner = new MissionRunner(this, this);
        missionBindHandler = new Handler();
        missionBindHandler.postDelayed(this::checkBinding, 50);
    }

    public void checkBinding(){
        if(missionRunner.isBinded()){
            missionRunner.loadMission();

        } else {
            missionBindHandler.postDelayed(this::checkBinding, 50);
        }
    }

    @Override
    public void statusUpdate(Task.TaskState state, String message) {
        Mission mission = MissionRunner.missionRunnerService.mission;
        missionStatus.setText(String.format("Current Mission: %s - %s - %s", mission.getTitle(), state.toString(), mission.getCurrentTaskName()));
    }
}
