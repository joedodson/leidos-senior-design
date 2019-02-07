package com.leidossd.dronecontrollerapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.leidossd.dronecontrollerapp.missions.Mission;
import com.leidossd.dronecontrollerapp.missions.SpecificMission;
import com.leidossd.dronecontrollerapp.missions.runner.MissionRunner;

import static com.leidossd.dronecontrollerapp.MainApplication.showToast;

public class MissionServiceTestActivity extends AppCompatActivity {

    MissionRunner missionRunner;

    private static final String CHANNEL_ID = "SOME_CHANNEL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_service_test);

        missionRunner = BootstrapApplication.getMissionRunnerInstance();
    }

    public void startService(View view) {
        missionRunner.startMission(getApplicationContext(), new SpecificMission( "SpecificMission"));
    }
}
