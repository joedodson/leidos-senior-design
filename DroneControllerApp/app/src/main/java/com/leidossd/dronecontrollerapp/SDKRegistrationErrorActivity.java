package com.leidossd.dronecontrollerapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class SDKRegistrationErrorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdk_registration_error);
    }

    public void retry(View view) {
        // TODO: implement retrying the sdk registration
        startActivity(new Intent(this, SplashActivity.class));
        finish();
    }
}