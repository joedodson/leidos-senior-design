package com.leidossd.dronecontrollerapp;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    MainApplication app = (MainApplication) getApplication();

    BroadcastReceiver permissionReceiver;
    BroadcastReceiver registrationReceiver;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startConnection(View view) {
        startActivity(new Intent(this, DroneConnectionActivity.class));
    }
}
