package com.leidossd.dronecontrollerapp;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.leidossd.dronecontrollerapp.missions.Mission;
import com.leidossd.dronecontrollerapp.missions.SpecificMission;

public class ConfirmMissionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NoActionBar);
        setContentView(R.layout.activity_confirm_mission);
    }

    public void onClicked(View view) {
        Intent intent = new Intent();
        SpecificMission mission = new SpecificMission("Waypoint Mission", null);
        intent.putExtra("Mission", mission);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
