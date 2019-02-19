package com.leidossd.dronecontrollerapp;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.leidossd.utils.MissionAction;

public class CreateMissionActivity extends AppCompatActivity implements WaypointFragment.WaypointFragmentListener {

    private Fragment baseFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NoActionBar);
        setContentView(R.layout.activity_create_mission);
        MissionAction action = (MissionAction) getIntent().getSerializableExtra("MissionType");
        defineFragment(action);
    }

    private void defineFragment(MissionAction action){
        if(action != null){
            switch(action){
                case WAYPOINT_MISSION:
                    //Load fragment here
                    baseFragment = new WaypointFragment();
                    break;
                case DEFAULT_MISSION:
                case CUSTOM_MISSION:
                    break;
            }
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
    public void onFragmentInteraction(Uri uri) {

    }
}
