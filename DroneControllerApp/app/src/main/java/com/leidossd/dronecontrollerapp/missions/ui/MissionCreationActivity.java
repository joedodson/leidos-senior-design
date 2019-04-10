package com.leidossd.dronecontrollerapp.missions.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.leidossd.dronecontrollerapp.R;
import com.leidossd.dronecontrollerapp.missions.Mission;

public class MissionCreationActivity extends AppCompatActivity implements MissionCreateListener {
    private static final String TAG = MissionCreationActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NoActionBar);
        setContentView(R.layout.activity_mission_creation);

        try {
            Class fragmentClass = (Class) getIntent().getSerializableExtra("Fragment Class");
            Fragment baseFragment = (Fragment) fragmentClass.newInstance();

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment, baseFragment)
                    .show(baseFragment)
                    .commit();

        } catch (Exception e) {
            Log.e(TAG, "Error while trying to instantiate fragment class from intent");
            finish();
        }
    }

    @Override
    public void createMission(Mission mission, boolean saveMission) {
        Intent intent = new Intent();
        intent.putExtra("Mission", mission);
        intent.putExtra("Save Mission", saveMission);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
