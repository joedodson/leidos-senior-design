package com.leidossd.dronecontrollerapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.leidossd.djiwrapper.FlightControllerWrapper;

import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;

public class DroneInput extends AppCompatActivity {

    private DroneState state = DroneState.ON;
    public enum DroneState {
        ON, READY, WAITING
    }

    private TextView testText;
    private EditText xBox;
    private EditText yBox;
    private EditText zBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drone_input);
        testText = findViewById(R.id.text_1);

        testText.setText("Off");
        xBox = findViewById(R.id.x_box);
        yBox = findViewById(R.id.y_box);
        zBox = findViewById(R.id.z_box);
        addCoordinateListener(xBox);
        addCoordinateListener(yBox);
        addCoordinateListener(zBox);
    }

    //TODO: Prevent buttons from being clicked at the wrong times.
    public void onClicked(View view) {
        switch (view.getId()) {
            case(R.id.button_1): { //Back Button
                if(getState() == DroneState.ON) {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                    break;
                }
            } case(R.id.button_3): { //Confirm button
                if(getState() == DroneState.READY){
                    setState(DroneState.WAITING);
                    //TODO: Ask about returning result from function, to know when its done executing
                    FlightControllerWrapper.getInstance().goToAbsoluteXYZ(new Coordinate(toIntEmpty(xBox), toIntEmpty(yBox), toIntEmpty(zBox)));
                }
                break;
            } case(R.id.button_4): { //Takeoff button
                if(getState() == DroneState.ON){
                    FlightControllerWrapper.getInstance().startTakeoff(new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            setState(DroneState.READY);
                            if(BuildConfig.DEBUG){
                                if (djiError != null) {
                                    showToast(djiError.getDescription());
                                } else {
                                    showToast("Take off success!");
                                }
                            }
                        }
                    });
                }
                break;
            } case(R.id.button_5): { //Landing button
                if(getState() == DroneState.READY){
                    FlightControllerWrapper.getInstance().startLanding(new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            setState(DroneState.ON);
                            if(BuildConfig.DEBUG){
                                if (djiError != null) {
                                    showToast(djiError.getDescription());
                                } else {
                                    showToast("Landing started.");
                                }
                            }
                        }
                    });
                }
                break;
            }
        }
    }

    private void addCoordinateListener(EditText box){
        box.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                checkTextStatus();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }
        });
    }

    private void checkTextStatus(){
        int x = toIntEmpty(xBox);
        int y = toIntEmpty(yBox);
        int z = toIntEmpty(zBox);

        if (x != 0 || y != 0) {
            zBox.setEnabled(false);
        } else {
            zBox.setEnabled(true);
        }
        if (z != 0) {
            xBox.setEnabled(false);
            yBox.setEnabled(false);
        } else {
            xBox.setEnabled(true);
            yBox.setEnabled(true);
        }
    }

    private int toIntEmpty(EditText box) {
        String s = box.getText().toString();
        return s.isEmpty() ? 0 : Integer.parseInt(s);
    }

    public DroneState getState() { return state; }

    public void setState(DroneState ds) { state = ds; }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
