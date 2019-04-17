package com.leidossd.djiwrapper;

import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import dji.common.flightcontroller.virtualstick.FlightControlData;
import dji.common.flightcontroller.virtualstick.FlightCoordinateSystem;
import dji.common.flightcontroller.virtualstick.RollPitchControlMode;
import dji.common.flightcontroller.virtualstick.VerticalControlMode;
import dji.common.flightcontroller.virtualstick.YawControlMode;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKManager;

public class VirtualStickFlightControl {
    private static final String TAG = VirtualStickFlightControl.class.getSimpleName();

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

    CommonCallbacks.CompletionCallback callbackFail;

    private boolean inFlight;
    private boolean enabled;

    private Timer inputTimer;
    private TimerTask inputTask;

    private VirtualStickFlightControl() {
        pitch = roll = yaw = throttle = 0;
        updatePeriod = 200;
        inFlight = false;
        flightController = ((Aircraft) DJISDKManager.getInstance().
                getProduct()).getFlightController();

        flightController.setRollPitchControlMode(RollPitchControlMode.VELOCITY);
        flightController.setVerticalControlMode(VerticalControlMode.VELOCITY);
        flightController.setYawControlMode(YawControlMode.ANGULAR_VELOCITY);
        inputTimer = null;
        inputTask = null;
        enabled = false;
    }

    public static VirtualStickFlightControl getInstance() {
        if (instance == null)
            instance = new VirtualStickFlightControl();
        return instance;
    }


    public void setListener(VirtualSticksIncrementListener listener) {
        this.listener = listener;
    }

    public void setCallbackFail(@Nullable CommonCallbacks.CompletionCallback callback) {
        callbackFail = callback;
    }

    public void enable() {
        if (enabled)
            return;
        roll = pitch = yaw = throttle = 0;
        inputTimer = new Timer();
        inputTask = new VirtualSticksUpdateTask();
        flightController.setVirtualStickModeEnabled(true, (error) -> {
            inputTimer.schedule(inputTask, 0, updatePeriod);
            enabled = true;
        });
    }

    public void disable() {
        if (!enabled)
            return;
        roll = pitch = yaw = throttle = 0;
        flightController.setVirtualStickModeEnabled(false, null);
        inputTimer.cancel();
        inputTimer.purge();
        inputTimer = null;
        inputTask = null;
        enabled = false;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setRollPitchCoordinateSystem(FlightCoordinateSystem fcs) {
        flightController.setRollPitchCoordinateSystem(fcs);
    }

    public float getSpeed() {
        return speed;
    }

    public float getAngularVelocity() {
        return angularVelocity;
    }

    public void setDirection(Coordinate direction) {
        Coordinate unitDirection = direction.unit();
        this.roll = unitDirection.getX() * speed;
        this.pitch = unitDirection.getY() * speed;
        this.throttle = unitDirection.getZ() * speed;
    }

    public void setYaw(float yaw) {
        Log.v(TAG,"Setting yaw to " + String.valueOf(yaw));
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public float getRoll() {
        return roll;
    }

    public float getYaw() {
        return yaw;
    }

    public float getThrottle() {
        return throttle;
    }

    public boolean isInFlight() {
        return inFlight;
    }

    public void halt() {
        pitch = roll = yaw = throttle = 0;
//        inFlight = false;
//        flightController.setVirtualStickModeEnabled(false, null);
    }

    class VirtualSticksUpdateTask extends TimerTask {
        @Override
        public void run() {
            if (flightController != null) {
//                flightController.setRollPitchControlMode(RollPitchControlMode.VELOCITY);
//                flightController.setVerticalControlMode(VerticalControlMode.VELOCITY);
//                flightController.setYawControlMode(YawControlMode.ANGULAR_VELOCITY);

                flightController.sendVirtualStickFlightControlData(
                        new FlightControlData(roll, pitch, yaw, throttle), (error) -> {
                            if (error != null) {
                                if (callbackFail != null)
                                    callbackFail.onResult(error);
                                halt();
                            }
                        });
                if (listener != null)
                    listener.increment(
                            new Coordinate(roll, pitch, throttle).scale((updatePeriod / (float) 1000.0)),
                            yaw * (updatePeriod / (float) 1000.0));
            }
        }
    }

    interface VirtualSticksIncrementListener {
        void increment(Coordinate positionDelta, float rotationDelta);
    }
}