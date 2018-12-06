package com.leidossd.djiwrapper;
import android.os.Handler;
import android.support.annotation.Nullable;
import java.lang.Math;

import dji.common.error.DJIError;
import dji.common.flightcontroller.virtualstick.RollPitchControlMode;
import dji.common.flightcontroller.virtualstick.VerticalControlMode;
import dji.common.flightcontroller.virtualstick.FlightControlData;
import dji.common.flightcontroller.virtualstick.YawControlMode;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.sdkmanager.DJISDKManager;
import dji.sdk.products.Aircraft;

public class FlightControllerWrapper {
    private static FlightControllerWrapper instance = null;

    private FlightController flightController;
    private FlightData flightData;

    private Coordinate position;
    private float directionFacing;

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
        this.directionFacing = 0;
        this.flightData = null;
        this.flightSpeed = (float) 0.5;
    }

    public Coordinate getPosition(){
        if(!isInFlight())
            return position;

        // return where it currently is in the flight path
        return flightData.currentPosition();
    }

    public boolean isInFlight(){
        return flightData != null;
    }

    public void rotateTo(float angle, @Nullable CommonCallbacks.CompletionCallback callback){
        flightController.setYawControlMode(YawControlMode.ANGLE);
        flightController.sendVirtualStickFlightControlData(
                new FlightControlData(0,0,0,angle),
                callback);
    }

    public void goToRelativeXYZ(Coordinate destination){
        // go to the location with respect to the drones own coordinate system (where forward is y+)

    }

    public void goToAbsoluteXYZ(Coordinate destination, final @Nullable CommonCallbacks.CompletionCallback callback){
        // get the movement vector
        Coordinate movement = destination.add(position.scale(-1));

        // distinguish between rotation lock and not
        //if(rotationLock) {
        //}
        //else{
        //    // rotate to face the point, then straight to it

        //    movement = new Coordinate(0,
        //                   new Coordinate(movement.getX(), movement.getY(), 0).magnitude(),
        //                   movement.getZ());
        //}

        final double flightTime = movement.magnitude()/flightSpeed;

        // the math works out
        float roll = (float) (movement.getX()/flightTime);
        float pitch = (float) (movement.getY()/flightTime);
        float throttle = (float) (movement.getZ()/flightTime);

        // Start up the engines going at speed

        flightData = new FlightData(getPosition(), destination, flightTime);

        setVelocity(roll, pitch, throttle, new CommonCallbacks.CompletionCallback() {
            // after velocity is set, let it run for the flightTime, then stop it.
            @Override
            public void onResult(DJIError djiError){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        halt(callback);
                    }
                }, (long) (flightTime*1000));
            }
        });

        // We will need to take into account "skidding" as the drone stops and overshoots the target.
        // Which we'll need to test manually, probably using tape on the floor and the camera

        // We will also need to be concerned with interrupting the flight, if we call halt from
        // somewhere else later, then tell the drone to move again, the halt inside of this function
        // may stop the drone mid flight later.
    }


    // Stop the current flight, set all velocities to zero
    public void halt(@Nullable CommonCallbacks.CompletionCallback callback){
        position = getPosition();
        flightData = null;

        setVelocity(0,0,0, callback);
    }

    public void setVelocity(float roll, float pitch, float throttle, @Nullable CommonCallbacks.CompletionCallback callback){
         flightController.setRollPitchControlMode(RollPitchControlMode.VELOCITY);
         flightController.setVerticalControlMode(VerticalControlMode.VELOCITY);

         flightController.sendVirtualStickFlightControlData(
                 new FlightControlData(roll, pitch, 0, throttle),
                 callback);
    }


    public void goToOrigin(@Nullable CommonCallbacks.CompletionCallback callback){
        goToAbsoluteXYZ(new Coordinate(0,0,0), callback);
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
}
