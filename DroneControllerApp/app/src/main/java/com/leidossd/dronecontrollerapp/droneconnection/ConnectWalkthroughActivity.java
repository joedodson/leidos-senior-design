package com.leidossd.dronecontrollerapp.droneconnection;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.leidossd.dronecontrollerapp.MainActivity;
import com.leidossd.dronecontrollerapp.R;
import com.leidossd.dronecontrollerapp.droneconnection.wireless.WirelessConnectActivity;

public class ConnectWalkthroughActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_walkthrough);
    }

    public void connect(View view) {
        startActivity(new Intent(this, WirelessConnectActivity.class));
    }

    public void skip(View view) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}

