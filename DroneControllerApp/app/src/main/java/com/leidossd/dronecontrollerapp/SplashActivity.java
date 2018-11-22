package com.leidossd.dronecontrollerapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import dji.common.error.DJIError;
import dji.common.error.DJISDKError;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.sdkmanager.DJISDKManager;

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

    private DJISDKManager.SDKManagerCallback DJISDKManagerCallback;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        DJISDKManagerCallback = new DJISDKManager.SDKManagerCallback() {

            //Listens to the SDK registration result
            @Override
            public void onRegister(final DJIError error) {
                if (error == DJISDKError.REGISTRATION_SUCCESS) {
                    Log.i(TAG, "SDK Registration Success");
                    DJISDKManager.getInstance().startConnectionToProduct();
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                } else {
                    Log.e(TAG, error.getDescription());
                    startActivity(new Intent(SplashActivity.this, SDKRegistrationErrorActivity.class));
                }
            }

            @Override
            public void onProductDisconnect() {
                showToast(TAG + " disconnect");
                Log.d("TAG", "onProductDisconnect");
            }

            @Override
            public void onProductConnect(BaseProduct baseProduct) {
                showToast(TAG + " connect");
                Log.d("TAG", String.format("onProductConnect newProduct:%s", baseProduct));
            }

            @Override
            public void onComponentChange(BaseProduct.ComponentKey componentKey, BaseComponent oldComponent,
                                          BaseComponent newComponent) {
                showToast(TAG + " change");
                if (newComponent != null) {
                    newComponent.setComponentListener(new BaseComponent.ComponentListener() {

                        @Override
                        public void onConnectivityChange(boolean isConnected) {
                            Log.d("TAG", "onComponentConnectivityChanged: " + isConnected);
                        }
                    });
                }

                Log.d("TAG",
                        String.format("onComponentChange key:%s, oldComponent:%s, newComponent:%s",
                                componentKey,
                                oldComponent,
                                newComponent));

            }
        };
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
            DJISDKManager.getInstance().registerApp(this, DJISDKManagerCallback);
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
        // If there is enough permission, we will start the registration
        if(missingPermissions.isEmpty()) {
            startRegistration();
        } else {
            Toast.makeText(this, "Missing Permissions!", Toast.LENGTH_LONG).show();
        }
    }

    private void startRegistration() {
        DJISDKManager.getInstance().registerApp(this, DJISDKManagerCallback);
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
