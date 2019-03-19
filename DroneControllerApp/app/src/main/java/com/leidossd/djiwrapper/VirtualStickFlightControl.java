package com.leidossd.djiwrapper;

import android.os.Handler;
import android.support.annotation.Nullable;

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

    private long updatePeriod;

    VirtualSticksIncrementListener listener;

    private boolean inFlight;
    private boolean enabled;

    private Timer inputTimer;

    VirtualSticksUpdateTask virtualSticksUpdateTask;

    private VirtualStickFlightControl(){
        pitch = roll = yaw = throttle = 0;
        updatePeriod = 200;
        inFlight = false;
        flightController = ((Aircraft) DJISDKManager.getInstance().
                                       getProduct()).getFlightController();
        inputTimer = new Timer();
        virtualSticksUpdateTask = new VirtualSticksUpdateTask();
        inputTimer.schedule(virtualSticksUpdateTask,0, updatePeriod);
    }

    public static VirtualStickFlightControl getInstance() {
        if(instance == null)
            instance = new VirtualStickFlightControl();
        return instance;
    }


    public void setListener(VirtualSticksIncrementListener listener){
        this.listener = listener;
    }

    public void enable(){
        roll = pitch = yaw = throttle = 0;
        flightController.setVirtualStickModeEnabled(true,null);
        enabled = true;
    }

    public void disable(){
        roll = pitch = yaw = throttle = 0;
        flightController.setVirtualStickModeEnabled(false, null);
        enabled = false;
    }

    public boolean isEnabled(){
        return enabled;
    }

    public void setRollPitchCoordinateSystem(FlightCoordinateSystem fcs){
        flightController.setRollPitchCoordinateSystem(fcs);
    }

    public float getSpeed(){
        return speed;
    }

    public float getAngularVelocity(){
        return angularVelocity;
    }

    public void setDirection(Coordinate direction){
        Coordinate unitDirection = direction.unit();
        this.roll = unitDirection.getX()*speed;
        this.pitch = unitDirection.getY()*speed;
        this.throttle = unitDirection.getZ()*speed;
    }

    public void setYaw(float yaw){
        this.yaw = yaw;
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

    public void halt(){
        pitch = roll = yaw = throttle = 0;
        inFlight = false;
        flightController.setVirtualStickModeEnabled(false, null);
    }

    class VirtualSticksUpdateTask extends TimerTask {
        @Override
        public void run(){
            if (flightController != null) {
                flightController.setRollPitchControlMode(RollPitchControlMode.VELOCITY);
                flightController.setVerticalControlMode(VerticalControlMode.VELOCITY);
                flightController.setYawControlMode(YawControlMode.ANGULAR_VELOCITY);

                flightController.sendVirtualStickFlightControlData(
                    new FlightControlData(roll, pitch, yaw, throttle), (error) -> {});
                listener.increment(
                        new Coordinate(roll,pitch,throttle).scale((float) (updatePeriod/1000.0)),
                        yaw*(updatePeriod/(float)1000.0));
            }
        }
    }

    interface VirtualSticksIncrementListener {
        void increment(Coordinate positionDelta, float rotationDelta);
    }
}