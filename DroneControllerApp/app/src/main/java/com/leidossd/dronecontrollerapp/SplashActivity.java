package com.leidossd.dronecontrollerapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import dji.sdk.sdkmanager.DJISDKManager;

import static utils.IntentAction.REGISTRATION_RESULT;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = SplashActivity.class.getName();

    private static final String[] REQUIRED_PERMISSION_LIST = new String[]{
            Manifest.permission.VIBRATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
    };
    private List<String> missingPermissions = new ArrayList<>();
    private static final int PERMISSION_REQUEST_CODE = 12345;

    private BroadcastReceiver registrationReceiver;

    private AtomicBoolean registrationSuccess;
    private AtomicBoolean permissionsGranted;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        registrationSuccess = new AtomicBoolean(false);
        permissionsGranted = new AtomicBoolean(false);

        registrationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent registrationIntent) {
                registrationSuccess.set(registrationIntent.getBooleanExtra(REGISTRATION_RESULT.getResultKey(), false));
                if (registrationSuccess.get()) {
                    Log.i(TAG, "SDK Registration Success");
                    if (permissionsGranted.get()) {
                        startMainActivityConditionally();
                    } else {
                        Log.d(TAG, "Waiting for permissions");
                    }
                } else {
                    startActivity(new Intent(SplashActivity.this, SDKRegistrationErrorActivity.class));
                    finish();
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(registrationReceiver, new IntentFilter(REGISTRATION_RESULT.getActionString()));
    }

    @Override
    protected void onStart() {
        super.onStart();
        startAnimations();
        checkAndRequestPermissions();
    }

    private void startAnimations() {
        TextView progressText = findViewById(R.id.progressBarText);

        Animation textAnimation = new AlphaAnimation(0.0f, 1.0f);
        textAnimation.setDuration(1000);

        progressText.startAnimation(textAnimation);
    }

    /**
     * Checks if there is any missing permissions, and
     * requests runtime permission if needed.
     */
    private void checkAndRequestPermissions() {
        // Check for permissions
        for (String eachPermission : REQUIRED_PERMISSION_LIST) {
            if (ContextCompat.checkSelfPermission(this, eachPermission) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(eachPermission);
            }
        }
        // Request for missing permissions
        if (missingPermissions.isEmpty()) {
            permissionsGranted.set(true);
            startMainActivityConditionally();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,
                    missingPermissions.toArray(new String[missingPermissions.size()]),
                    PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Result of runtime permission request
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Check for granted permission and remove from missing list
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int i = grantResults.length - 1; i >= 0; i--) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    missingPermissions.remove(permissions[i]);
                }
            }
        }
        // If no missing permissions, continue to MainActivity
        if(missingPermissions.isEmpty()) {
            permissionsGranted.set(true);
            startMainActivityConditionally();
        } else {
            startActivity(new Intent(SplashActivity.this, SDKRegistrationErrorActivity.class));
            finish();
        }
    }

    public void startMainActivityConditionally() {
        if(registrationSuccess.get() && permissionsGranted.get()) {
            DJISDKManager.getInstance().startConnectionToProduct();
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        } else {
            Log.d(TAG, String.format("Couldn't start main activity, registrationSuccess: %s, permissionGranted: %s",
                    String.valueOf(registrationSuccess), String.valueOf(permissionsGranted)));
        }
    }
}
