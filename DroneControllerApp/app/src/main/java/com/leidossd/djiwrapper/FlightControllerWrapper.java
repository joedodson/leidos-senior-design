package com.leidossd.djiwrapper;
import android.os.Handler;
import android.support.annotation.Nullable;

import dji.common.error.DJIError;

import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.sdkmanager.DJISDKManager;
import dji.sdk.products.Aircraft;

public class FlightControllerWrapper {
    private static FlightControllerWrapper instance = null;

    private FlightController flightController;
    private FlightData flightData;

    private Coordinate position;
    private Coordinate directionVector;

    private boolean rotationLock;

    private float flightSpeed;

    public static FlightControllerWrapper getInstance(){
        if(instance == null)
            instance = new FlightControllerWrapper();

        return instance;
    }

    private FlightControllerWrapper(){
        flightController = ((Aircraft) DJISDKManager.getInstance().
                                       getProduct()).getFlightController();
        this.position = new Coordinate(0,0,0);
        this.rotationLock = true;
        this.directionVector = new Coordinate(0,1,0);
        this.flightData = null;
        this.flightSpeed = (float) 0.5;
    }


    // Here lie forwarded functions, add them as you need them

    /* To use these functions, DJI makes the callbacks like this:
       new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
            }
       });

       sometimes there are things inside that onResult() function, depends on what you want.
       Also note that ALL of what's written above appears as a single argument passed to a function.
     */

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

    public void getCompassCalibrationState(@Nullable CommonCallbacks.CompletionCallback callback){
        flightController.getCompass().getCalibrationState();
    }

    public void compassStartCalibration(CommonCallbacks.CompletionCallback callback){
        flightController.getCompass().startCalibration(callback);
    }

    public void compassStopCalibration(CommonCallbacks.CompletionCallback callback){
        flightController.getCompass().stopCalibration(callback);
    }
}
