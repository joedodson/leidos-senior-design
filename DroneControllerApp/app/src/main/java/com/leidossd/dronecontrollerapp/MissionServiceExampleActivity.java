package com.leidossd.dronecontrollerapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.leidossd.djiwrapper.Coordinate;
import com.leidossd.dronecontrollerapp.missions.Mission;
import com.leidossd.dronecontrollerapp.missions.SpecificMission;
import com.leidossd.dronecontrollerapp.missions.MissionRunner;
import com.leidossd.dronecontrollerapp.missions.Task;
import com.leidossd.dronecontrollerapp.missions.WaypointMission;

public class MissionServiceExampleActivity extends AppCompatActivity implements Task.StatusUpdateListener {

    MissionRunner missionRunner;
    TextView missionStatusTextView;
    Mission mission;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_service_example);

        missionStatusTextView = findViewById(R.id.tv_activity_mission_service_example_status);

        mission = new SpecificMission("SpecificMission");
//        mission = new WaypointMission(new Coordinate(1,1,0));
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
