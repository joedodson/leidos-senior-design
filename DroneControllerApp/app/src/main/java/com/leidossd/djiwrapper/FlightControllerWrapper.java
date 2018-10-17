package com.leidossd.djiwrapper;
import android.support.annotation.Nullable;

import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightController;

public class FlightControllerWrapper {
    private FlightController flightController;
    private float x;
    private float y;
    private float z;

    public FlightControllerWrapper(FlightController fc){
        flightController = fc;
        x = 0;
        y = 0;
        z = 0;
    }

    public void gotoX(float x){

    }

    public void gotoY(float y){

    }

    public void gotoZ(float z){

    }

    public void gotoXYZ(float x, float y, float z){
        gotoX(x);
        gotoY(y);
        gotoZ(z);
    }



    // Here lie forwarded functions

    public void turnOnMotors(@Nullable CommonCallbacks.CompletionCallback callback){
        flightController.turnOnMotors(callback);
    }

    public void turnOffMotors(@Nullable CommonCallbacks.CompletionCallback callback){
        flightController.turnOffMotors(callback);
    }

    public void startTakeoff(@Nullable CommonCallbacks.CompletionCallback callback){
        flightController.startTakeoff(callback);
    }

    public void cancelTakeoff(@Nullable CommonCallbacks.CompletionCallback callback){
        flightController.cancelTakeoff(callback);
    }

    public void startLanding(@Nullable CommonCallbacks.CompletionCallback callback){
        flightController.startLanding(callback);
    }

    public void cancelLanding(@Nullable CommonCallbacks.CompletionCallback callback){
        flightController.cancelLanding(callback);
    }

    public void confirmLanding(@Nullable CommonCallbacks.CompletionCallback callback){
        flightController.confirmLanding(callback);
    }
}
