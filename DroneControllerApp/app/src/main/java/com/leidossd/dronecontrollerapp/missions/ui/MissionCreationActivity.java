package com.leidossd.dronecontrollerapp.missions.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.leidossd.dronecontrollerapp.R;
import com.leidossd.dronecontrollerapp.missions.Mission;

public class MissionCreationActivity extends AppCompatActivity implements MissionCreateListener{

    private Fragment baseFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NoActionBar);
        setContentView(R.layout.activity_mission_creation);

        try {
            Class fragmentClass = (Class) getIntent().getSerializableExtra("Fragment Class");
            baseFragment = (Fragment) fragmentClass.newInstance();
        } catch (IllegalAccessException|InstantiationException|ClassCastException e) {
            throw new RuntimeException("Error while trying to instantiate fragment class from intent");
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
