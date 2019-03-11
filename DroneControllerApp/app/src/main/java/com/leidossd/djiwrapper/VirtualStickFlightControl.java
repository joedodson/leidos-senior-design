package com.leidossd.djiwrapper;

import java.util.Timer;
import java.util.TimerTask;

import dji.common.error.DJIError;
import dji.common.flightcontroller.virtualstick.FlightCoordinateSystem;
import dji.common.flightcontroller.virtualstick.RollPitchControlMode;
import dji.common.flightcontroller.virtualstick.VerticalControlMode;
import dji.common.flightcontroller.virtualstick.FlightControlData;
import dji.common.flightcontroller.virtualstick.YawControlMode;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKManager;

public class VirtualStickFlightControl  {
    static private VirtualStickFlightControl instance = null;
    private FlightController flightController = null;

    // m/s, probably should make a constant for the speed and angular velocity, no magic numbers
    private float speed = (float) .5;
    // deg/s
    private float angularVelocity = 50;

    private float pitch;
    private float roll;
    private float yaw;
    private float throttle;

    private boolean inFlight;

    private Timer inputTimer = null;
    private Timer endTimer;

    VirtualSticksUpdateTask virtualSticksUpdateTask;

    private VirtualStickFlightControl(){
        pitch = roll = yaw = throttle = 0;
        inFlight = false;
        flightController = ((Aircraft) DJISDKManager.getInstance().
                                       getProduct()).getFlightController();
        inputTimer = new Timer();
        virtualSticksUpdateTask = new VirtualSticksUpdateTask();
        inputTimer.schedule(virtualSticksUpdateTask,0, 200);
    }

    public static VirtualStickFlightControl getInstance() {
        if(instance == null)
            instance = new VirtualStickFlightControl();
        return instance;
    }

    public float getPitch(){
        return pitch;
    }

    public float getRoll(){
        return roll;
    }

    public float getYaw(){
        return yaw;
    }

    public float getThrottle(){
        return throttle;
    }

    public boolean isInFlight(){
        return inFlight;
    }

    public void move(Coordinate movement, CommonCallbacks.CompletionCallback callback){
       move(movement, FlightCoordinateSystem.BODY, callback);
    }

    public void move(Coordinate movement, FlightCoordinateSystem flightCoordinateSystem, CommonCallbacks.CompletionCallback callback){
        flightController.setRollPitchCoordinateSystem(flightCoordinateSystem);

        float duration = (float) movement.magnitude()/speed;

        float p = (float) movement.getY()/duration;
        float r = (float) movement.getX()/duration;
        float t = (float) movement.getZ()/duration;

        startTask(r,p,0, t, (long) (duration*1000), callback);
    }

    public void rotate(float theta, CommonCallbacks.CompletionCallback callback){
        float duration = (float) theta/angularVelocity;

        startTask(0,0, angularVelocity,0, (long) (duration*1000), callback);
    }

    public void halt(){
        pitch = roll = yaw = throttle = 0;
        inFlight = false;
        flightController.setVirtualStickModeEnabled(false, null);
    }

    private void startTask(float roll, float pitch, float yaw, float throttle, long duration, CommonCallbacks.CompletionCallback callback){
        // make sure we aren't doing yaw with anything else simultaneously
        // may need to throw some kind of exception, for now just do nothing.
        if(yaw != 0 && (pitch != 0 || roll != 0 || throttle != 0))
            return;

        // TODO: make sure that no other task is already running, more rigorously
        if(isInFlight())
            return;

        // enable flight before starting. may break out into separate functions to enable/disable
        flightController.setVirtualStickModeEnabled(true, null);
        // changing these values changes the flight in real time
        this.pitch = pitch;
        this.roll = roll;
        this.throttle = throttle;
        this.yaw = yaw;

        this.inFlight = true;

        // schedule a halt after the time has passed
        endTimer = new Timer();
        endTimer.schedule(new VirtualSticksClearTask(callback), duration);
    }

    class VirtualSticksClearTask extends TimerTask {
        CommonCallbacks.CompletionCallback callback;

        VirtualSticksClearTask(CommonCallbacks.CompletionCallback callback){
            this.callback = callback;
        }
        @Override
        public void run(){
            halt();
            callback.onResult(null);
        }
    }

    class VirtualSticksUpdateTask extends TimerTask {
        @Override
        public void run(){
            if (flightController != null) {
                flightController.setRollPitchControlMode(RollPitchControlMode.VELOCITY);
                flightController.setVerticalControlMode(VerticalControlMode.VELOCITY);
                flightController.setYawControlMode(YawControlMode.ANGULAR_VELOCITY);

                flightController.sendVirtualStickFlightControlData(
                    new FlightControlData(
                            roll, pitch, yaw, throttle
                    ), new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                        }
                    }
            );
        }
        }
    }
}