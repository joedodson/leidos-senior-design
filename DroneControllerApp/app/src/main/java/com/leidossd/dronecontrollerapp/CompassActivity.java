package com.leidossd.dronecontrollerapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.leidossd.djiwrapper.Coordinate;
import com.leidossd.djiwrapper.FlightControllerWrapper;

import dji.common.error.DJIError;
import dji.common.flightcontroller.CompassCalibrationState;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.Compass;
import dji.sdk.products.Aircraft;

import static com.leidossd.dronecontrollerapp.MainApplication.showToast;

public class CompassActivity extends AppCompatActivity implements View.OnClickListener {

    Compass compass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        Aircraft aircraft = MainApplication.getDroneInstance();
        if(aircraft != null && aircraft.getFlightController() != null && aircraft.getFlightController().getCompass() != null){
            compass = aircraft.getFlightController().getCompass();
        }
        else
            compass = null;
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.btn_compass_calib_start:
                compass.startCalibration(new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if(djiError == null)
                            showToast("Compass Calibration Started");
                        else
                            showToast(djiError.getDescription());
                    }
                });
                break;
            case R.id.btn_compass_calib_stop:
                compass.stopCalibration(new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if(djiError == null)
                            showToast("Compass Calibration Ended");
                        else
                            showToast(djiError.getDescription());
                    }
                });
                break;

            case R.id.btn_compass_calib_status:
                if (compass != null) {
                    CompassCalibrationState state = compass.getCalibrationState();
                    switch(state){
                        case FAILED:
                            showToast("Compass Calibration State: FAILED");
                            break;
                        case HORIZONTAL:
                            showToast("Compass Calibration State: HORIZONTAL");
                            break;
                        case UNKNOWN:
                            showToast("Compass Calibration State: UNKNOWN");
                            break;
                        case VERTICAL:
                            showToast("Compass Calibration State: VERTICAL");
                            break;
                        case SUCCESSFUL:
                            showToast("Compass Calibration State: SUCCESSFUL");
                            break;
                        case NOT_CALIBRATING:
                            showToast("Compass Calibration State: NOT_CALIBRATING");
                            break;
                    }
                }
                break;

            default:
                break;
        }

    }
}
