package com.leidossd.djiwrapper;

import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import dji.common.flightcontroller.CompassCalibrationState;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKManager;

public class FlightControllerWrapper implements DeadReckoningFlightControl.PositionListener, DeadReckoningFlightControl.DirectionListener {
    private static FlightControllerWrapper instance = null;

    private FlightController flightController;
    // hacky, gotta replace this with the interface
    private DeadReckoningFlightControl coordinateFlightControl;
    private PositionListener positionListener = null;
    private DirectionListener directionListener = null;
    private boolean isAirborne = false;

    private LatLng home;

    public static FlightControllerWrapper getInstance() {
        if (instance == null)
            instance = new FlightControllerWrapper();

        return instance;
    }

    private FlightControllerWrapper() {
        flightController = ((Aircraft) DJISDKManager.getInstance().
                getProduct()).getFlightController();
        coordinateFlightControl = new DeadReckoningFlightControl();
        coordinateFlightControl.setPositionListener(this);
        coordinateFlightControl.setDirectionListener(this);
        home = null;
    }

    public boolean isHomeSet(){
        return home != null;
    }

    public void setHome(LatLng newHome){
        this.home = newHome;
    }

    public LatLng getHome(){
        return this.home;
    }

    public void syncDirection(){
        coordinateFlightControl.setDirection(new Coordinate(0,1,0)
                .rotateByAngle(flightController.getCompass().getHeading()));
    }

    public void setPositionListener(PositionListener listener) {
        this.positionListener = listener;
    }

    public void setDirectionListener(DirectionListener listener){
        this.directionListener = listener;
    }

    public boolean isInFlight() {
        return coordinateFlightControl.isInFlight();
    }

    public void haltFlight() {
        coordinateFlightControl.halt();
    }

    public void gotoRelativeXYZ(Coordinate destination, @Nullable CommonCallbacks.CompletionCallback callback) {
        coordinateFlightControl.setFlightMode(CoordinateFlightControl.FlightMode.RELATIVE);
        coordinateFlightControl.goTo(destination, callback);
    }

    public void gotoAbsoluteXYZ(Coordinate destination, @Nullable CommonCallbacks.CompletionCallback callback) {
        coordinateFlightControl.setFlightMode(CoordinateFlightControl.FlightMode.ABSOLUTE);
        coordinateFlightControl.goTo(destination, callback);
    }

    public void gotoXYZ(Coordinate destination, @Nullable CommonCallbacks.CompletionCallback callback) {
        coordinateFlightControl.goTo(destination, callback);
    }

    public void rotateTo(float angle, @Nullable CommonCallbacks.CompletionCallback callback) {
        coordinateFlightControl.rotateTo(angle, callback);
    }

    public void rotateBy(float angle, @Nullable CommonCallbacks.CompletionCallback callback){
        coordinateFlightControl.rotateBy(angle, callback);
    }

    public void setRotationLock(boolean rotationLock){
        coordinateFlightControl.setRotationLock(rotationLock);
    }
    public void setFlightMode(CoordinateFlightControl.FlightMode flightMode) {
        coordinateFlightControl.setFlightMode(flightMode);
    }

    public void setPosition(Coordinate position){
        coordinateFlightControl.setPosition(position);
    }

    public float getAngleFacing(){
        return coordinateFlightControl.getAngleFacing();
    }

    public Coordinate getPosition() {
        return coordinateFlightControl.getPosition();
    }

    public boolean isAirborne() {
        return isAirborne;
    }

    public interface PositionListener {
        public void updatePosition(Coordinate position);
    }

    public interface DirectionListener {
        public void updateDirection(float angle);
    }

    @Override
    public void updatePosition(Coordinate position) {
        if (positionListener != null)
            positionListener.updatePosition(position);
    }

    @Override
    public void updateDirection(float angle){
        if(directionListener != null)
            directionListener.updateDirection(angle);
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

    public void turnOnMotors(@Nullable CommonCallbacks.CompletionCallback callback) {
        flightController.turnOnMotors(callback);
    }

    public void turnOffMotors(@Nullable CommonCallbacks.CompletionCallback callback) {
        flightController.turnOffMotors(callback);
    }

    public void startTakeoff(@Nullable CommonCallbacks.CompletionCallback callback) {
        flightController.startTakeoff(callback);
    }

    public void cancelTakeoff(@Nullable CommonCallbacks.CompletionCallback callback) {
        flightController.cancelTakeoff(callback);
    }

    public void startLanding(@Nullable CommonCallbacks.CompletionCallback callback) {
        coordinateFlightControl.halt();
        flightController.startLanding(callback);
    }

    public void cancelLanding(@Nullable CommonCallbacks.CompletionCallback callback) {
        flightController.cancelLanding(callback);
    }

    public void confirmLanding(@Nullable CommonCallbacks.CompletionCallback callback) {
        flightController.confirmLanding(callback);
    }

    public boolean compassHasError() {
        return flightController.getCompass().hasError();
    }

    public CompassCalibrationState getCompassCalibrationState(@Nullable CommonCallbacks.CompletionCallback callback) {
        return flightController.getCompass().getCalibrationState();
    }

    public void compassStartCalibration(CommonCallbacks.CompletionCallback callback) {
        flightController.getCompass().startCalibration(callback);
    }

    public void compassStopCalibration(CommonCallbacks.CompletionCallback callback) {
        flightController.getCompass().stopCalibration(callback);
    }

    public void compassSetCalibrationStateCallback(@Nullable CompassCalibrationState.Callback callback) {
        flightController.getCompass().setCalibrationStateCallback(callback);
    }
}
