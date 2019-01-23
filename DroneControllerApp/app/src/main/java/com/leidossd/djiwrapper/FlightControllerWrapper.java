package com.leidossd.djiwrapper;
import android.os.Handler;
import android.support.annotation.Nullable;
import java.lang.Math;
import java.util.Timer;
import java.util.TimerTask;

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

    public void rotateBy(float angle, @Nullable CommonCallbacks.CompletionCallback callback){
        // TODO: Joe will use VirtualStick stuff to do yaw rotation
        flightController.setYawControlMode(YawControlMode.ANGLE);
        flightController.sendVirtualStickFlightControlData(
                new FlightControlData(0,0,0,angle),
                callback);
    }

    public void goToRelativeXYZ(Coordinate movement){
        // TODO: Joe will to use the VirtualStick stuff to do roll/pitch/throttle

        // go to the location with respect to the drones own coordinate system (where forward is y+)
    }

    public void goToAbsoluteXYZ(Coordinate destination, final @Nullable CommonCallbacks.CompletionCallback callback){
        // TODO: Dalton uses Joe's relative goto function and keeps track of coordinates

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



        // TODO: use Joe's relative goto instead of managing speeds yourself
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
        // TODO: the purpose of this function is in limbo, should it stop a current flight
        //       or be the killswitch?

        position = getPosition();
        flightData = null;

    }

    public void setVelocity(float roll, float pitch, float throttle, @Nullable CommonCallbacks.CompletionCallback callback){
         // TODO: Joe, use VirtualSticks to set roll/pitch/throttle speeds

         flightController.setRollPitchControlMode(RollPitchControlMode.VELOCITY);
         flightController.setVerticalControlMode(VerticalControlMode.VELOCITY);

         flightController.sendVirtualStickFlightControlData(
                 new FlightControlData(roll, pitch, 0, throttle),
                 callback);
    }


    public void goToOrigin(@Nullable CommonCallbacks.CompletionCallback callback){
        goToAbsoluteXYZ(new Coordinate(0,0,0), callback);
    }

    // TODO: define VirtualStickTasks similar to  how they are in the SimulatorActivity file
    //       except so that we can control it without having a listener for input

    class FlightTask extends TimerTask {
        // TODO: make singleton, add task queue
        private float pitch;
        private float roll;
        private float yaw;
        private float throttle;

        private boolean inFlight;

        Timer dataTimer;
        Timer endTimer;

        public FlightTask(){
            pitch = roll = yaw = throttle = 0;
            inFlight = false;

            // this is always running
            dataTimer = new Timer();
            dataTimer.schedule(this, 0, 200);
        }

        // TODO: add callback
        void setTask(float pitch, float roll, float throttle, float yaw, long duration){
            // make sure we aren't doing yaw with anything else simultaneously
            // may need to throw some kind of exception, for now just do nothing.
            if(yaw != 0 && (pitch != 0 || roll != 0 || throttle != 0))
                return;

            // changing these values changes the flight
            this.pitch = pitch;
            this.roll = roll;
            this.throttle = throttle;
            this.yaw = yaw;

            this.inFlight = true;

            // schedule a halt after the time has passed
            endTimer = new Timer();
            endTimer.schedule(new FlightHalt(), duration);
        }

        public boolean isInFlight(){
            return inFlight;
        }

        public void halt(){
            // stop flight
            pitch = roll = yaw = throttle = 0;
            inFlight = false;
        }

        @Override
        public void run(){
            if (flightController != null) {
                flightController.sendVirtualStickFlightControlData(
                        new FlightControlData(
                                pitch, roll, yaw, throttle
                        ), new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                            }
                        }
                );
            }
        }

        class FlightHalt extends TimerTask {
            @Override
            public void run(){
                halt();
            }
        }
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
