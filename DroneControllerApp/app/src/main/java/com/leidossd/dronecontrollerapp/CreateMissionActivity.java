package com.leidossd.dronecontrollerapp;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.leidossd.dronecontrollerapp.missions.Mission;
import com.leidossd.utils.MissionAction;

public class CreateMissionActivity extends AppCompatActivity implements MissionCreateListener {

    private Fragment baseFragment;
    private FragmentManager fragmentManager;
    private MissionAction missionType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NoActionBar);
        setContentView(R.layout.activity_create_mission);
        MissionAction action = (MissionAction) getIntent().getSerializableExtra("Mission Type");
        defineFragment(action);
    }

    private void defineFragment(MissionAction action){
        if(action != null){
            //Load fragment here
            switch(action){
                case WAYPOINT_MISSION:
                    baseFragment = new WaypointFragment();
                    break;
                case CUSTOM_MISSION:
                    break;
            }
            missionType = action;
        } else {
            throw new RuntimeException("Must define mission type when creating an activity.");
        }

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.fragment, baseFragment)
                .show(baseFragment)
                .commit();
    }

    @Override
    public void createMission(Mission mission, boolean saveMission){
        Intent intent = new Intent();
        intent.putExtra("Mission", mission);
        intent.putExtra("Save Mission", saveMission);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
