package com.leidossd.dronecontrollerapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.dji.sdk.sample.internal.utils.ModuleVerificationUtil;
import com.leidossd.djiwrapper.FlightControllerWrapper;

import dji.sdk.flightcontroller.FlightController;

public class SecondActivity extends AppCompatActivity {

    private boolean engineOn = false;
    private TextView testText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        testText = findViewById(R.id.text_1);
        testText.setText("Off");
    }

    public void onClicked(View view) {
        switch (view.getId()) {
            case(R.id.button_1): { //On Button
                if(!engineOn) {
                    FlightControllerWrapper.getInstance().turnOnMotors(null);
                    engineOn = true;
                    testText.setText("On");
                }
                break;
            }
            case(R.id.button_2): { //Off Button
                if(engineOn) {
                    FlightControllerWrapper.getInstance().turnOffMotors(null);
                    engineOn = false;
                    testText.setText("Off");
                }
                break;
            }
            case(R.id.button_3): { //Back button
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
            }
        }
    }
}
