package com.leidossd.dronecontrollerapp.missions.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.leidossd.dronecontrollerapp.R;
import com.leidossd.dronecontrollerapp.missions.Mission;
import com.leidossd.dronecontrollerapp.missions.execution.MissionRunner;
import com.leidossd.dronecontrollerapp.missions.Task;

public class MissionStatusActivity extends AppCompatActivity implements Task.StatusUpdateListener {
    private TextView missionStatus;
    private Mission mission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_status);

        missionStatus = findViewById(R.id.mission_status);
        MissionRunner missionRunner = new MissionRunner(this);

        mission = missionRunner.getCurrentMission();
        if(mission != null) {
            updateMissionStatus(mission.getCurrentState().toString());
            missionRunner.getCurrentMission().addListener(this);
        } else {
            missionStatus.setText("No active mission found");
        }
    }

    @Override
    public void statusUpdate(Task.TaskState state, String message) {
        runOnUiThread(() -> updateMissionStatus(state.toString()));
    }

    private void updateMissionStatus(String stateString) {
        if (mission != null) {
            missionStatus.setText(String.format("Current Mission: %s - %s - %s",
                    mission.getTitle(),
                    stateString,
                    mission.getCurrentTaskName()));
        }
    }
}
