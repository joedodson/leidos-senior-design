package com.leidossd.djiwrapper;

import java.util.Timer;
import java.util.TimerTask;

import dji.common.error.DJIError;
import dji.common.flightcontroller.virtualstick.RollPitchControlMode;
import dji.common.flightcontroller.virtualstick.VerticalControlMode;
import dji.common.flightcontroller.virtualstick.FlightControlData;
import dji.common.flightcontroller.virtualstick.YawControlMode;
import dji.common.util.CommonCallbacks;

class VirtualStickFlightControl implements LowLevelFlightControl {
    static private VirtualStickFlightControl instance = null;

    // m/s
    private float speed = (float) .5;
    // deg/s
    private float angularVelocity = 50;

    private float pitch;
    private float roll;
    private float yaw;
    private float throttle;

    private boolean inFlight;

    Timer inputTimer;
    Timer endTimer;

    VirtualSticksUpdateTask virtualSticksUpdateTask;

    private VirtualStickFlightControl(){
        pitch = roll = yaw = throttle = 0;
        inFlight = false;

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

    public void move(Coordinate movement){
        float duration = (float) movement.magnitude()/speed;

        float p = (float) movement.getY()/duration;
        float r = (float) movement.getX()/duration;
        float t = (float) movement.getZ()/duration;

        startTask(p,r,0, t, (long) (duration*1000));
    }

    public void rotate(float theta){
        float duration = (float) theta/angularVelocity;

        startTask(0,0, angularVelocity,0, (long) (duration*1000));
    }

    public void halt(){
        pitch = roll = yaw = throttle = 0;
        inFlight = false;
    }

    private void startTask(float pitch, float roll, float yaw, float throttle, long duration){
        // make sure we aren't doing yaw with anything else simultaneously
        // may need to throw some kind of exception, for now just do nothing.
        if(yaw != 0 && (pitch != 0 || roll != 0 || throttle != 0))
            return;

        // TODO: make sure that no other task is already running, more rigorously
        if(isInFlight())
            return;

        // changing these values changes the flight
        this.pitch = pitch;
        this.roll = roll;
        this.throttle = throttle;
        this.yaw = yaw;

        this.inFlight = true;

        // schedule a halt after the time has passed
        endTimer = new Timer();
        endTimer.schedule(new VirtualSticksClearTask(), duration);
    }

    class VirtualSticksClearTask extends TimerTask {
        @Override
        public void run(){
            halt();
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
                            pitch, roll, yaw, throttle
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