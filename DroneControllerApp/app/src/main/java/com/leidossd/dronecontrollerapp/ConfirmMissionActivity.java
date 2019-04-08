package com.leidossd.dronecontrollerapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.leidossd.dronecontrollerapp.missions.Mission;

public class ConfirmMissionActivity extends AppCompatActivity {

    Mission mission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NoActionBar);
        setContentView(R.layout.activity_confirm_mission);
        TextView title = findViewById(R.id.mission_title);
        mission = getIntent().getParcelableExtra("Mission");
        title.setText(mission.getTitle());
    }

    public void onClicked(View view) {
        Intent intent = new Intent();
        intent.putExtra("Mission", mission);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
