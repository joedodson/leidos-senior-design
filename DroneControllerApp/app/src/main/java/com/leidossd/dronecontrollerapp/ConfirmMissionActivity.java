package com.leidossd.dronecontrollerapp;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class ConfirmMissionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NoActionBar);
        setContentView(R.layout.activity_confirm_mission);
    }

    public void onClicked(View view) {
        Intent intent = new Intent();
//        resultIntent.putExtra("Mission", "EEE");
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
