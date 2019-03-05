package com.leidossd.djiwrapper;
import android.os.Handler;
import android.support.annotation.Nullable;

import dji.common.error.DJIError;

import dji.common.flightcontroller.CompassCalibrationState;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.sdkmanager.DJISDKManager;
import dji.sdk.products.Aircraft;

public class FlightControllerWrapper {
    private static FlightControllerWrapper instance = null;

    private FlightController flightController;
    // hacky, gotta replace this with the interface
    private DeadReckoningFlightControl coordinateFlightControl;

    public static FlightControllerWrapper getInstance(){
        if(instance == null)
            instance = new FlightControllerWrapper();

        return instance;
    }

    private FlightControllerWrapper(){
        flightController = ((Aircraft) DJISDKManager.getInstance().
                                       getProduct()).getFlightController();
        this.coordinateFlightControl = new DeadReckoningFlightControl();
    }

    public boolean isInFlight(){
        return coordinateFlightControl.isInFlight();
    }

    public void haltFlight(){
        coordinateFlightControl.halt();
    }

    public void gotoRelativeXYZ(Coordinate destination){
        coordinateFlightControl.setFlightMode(CoordinateFlightControl.FlightMode.RELATIVE);
        coordinateFlightControl.goTo(destination);
    }

    public void gotoAbsoluteXYZ(Coordinate destination){
        coordinateFlightControl.setFlightMode(CoordinateFlightControl.FlightMode.ABSOLUTE);
        coordinateFlightControl.goTo(destination);
    }

    public void gotoXYZ(Coordinate destination){
        coordinateFlightControl.goTo(destination);
    }

    public void rotateTo(float angle){
        coordinateFlightControl.rotateTo(angle);
    }

    public void setFlightMode(CoordinateFlightControl.FlightMode flightMode){
        coordinateFlightControl.setFlightMode(flightMode);
    }

    public Coordinate getPosition(){
        return coordinateFlightControl.getPosition();
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
        coordinateFlightControl.halt();
        flightController.startLanding(callback);
    }

    public void cancelLanding(@Nullable CommonCallbacks.CompletionCallback callback){
        flightController.cancelLanding(callback);
    }

    public void confirmLanding(@Nullable CommonCallbacks.CompletionCallback callback){
        flightController.confirmLanding(callback);
    }

    public boolean compassHasError(){
        return flightController.getCompass().hasError();
    }

    public CompassCalibrationState getCompassCalibrationState(@Nullable CommonCallbacks.CompletionCallback callback){
        return flightController.getCompass().getCalibrationState();
    }

    public void compassStartCalibration(CommonCallbacks.CompletionCallback callback){
        flightController.getCompass().startCalibration(callback);
    }

    public void compassStopCalibration(CommonCallbacks.CompletionCallback callback){
        flightController.getCompass().stopCalibration(callback);
    }
}
