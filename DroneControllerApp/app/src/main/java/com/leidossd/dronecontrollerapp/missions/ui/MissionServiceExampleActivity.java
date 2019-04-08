package com.leidossd.dronecontrollerapp.missions.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.leidossd.djiwrapper.Coordinate;
import com.leidossd.dronecontrollerapp.MenuActivity;
import com.leidossd.dronecontrollerapp.R;
import com.leidossd.dronecontrollerapp.missions.Mission;
import com.leidossd.dronecontrollerapp.missions.MissionRunner;
import com.leidossd.dronecontrollerapp.missions.SpecificMission;
import com.leidossd.dronecontrollerapp.missions.Task;
import com.leidossd.dronecontrollerapp.missions.WaypointMission;

public class MissionServiceExampleActivity extends MenuActivity implements Task.StatusUpdateListener {

    MissionRunner missionRunner;
    TextView missionStatusTextView;
    WaypointMission mission;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_service_example);


        missionStatusTextView = findViewById(R.id.tv_activity_mission_service_example_status);

        mission = new WaypointMission(new Coordinate(0,3,0));
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
