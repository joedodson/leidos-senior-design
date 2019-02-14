package com.leidossd.dronecontrollerapp;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.Fragment;

import com.leidossd.utils.MissionAction;

public class CreateMissionActivity extends Activity {

    private Fragment baseFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_mission);
        MissionAction action = (MissionAction) getIntent().getSerializableExtra("MissionType");
        defineFragment(action);
    }

    private void defineFragment(MissionAction action){
        if(action != null){
            switch(action){
                case WAYPOINT_MISSION:
                    //Load fragment here
                    //baseFragment
                    break;
                case DEFAULT_MISSION:
                case CUSTOM_MISSION:
                    break;
            }
        } else {
            throw new RuntimeException("Must define mission type when creating an activity.");
        }
    }
}
