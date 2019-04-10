package com.leidossd.djiwrapper;


import android.support.annotation.Nullable;
import android.util.Log;

import com.leidossd.dronecontrollerapp.missions.RotationTask;

import java.util.Timer;
import java.util.TimerTask;

import dji.common.flightcontroller.virtualstick.FlightCoordinateSystem;
import dji.common.util.CommonCallbacks;

public class DeadReckoningFlightControl implements CoordinateFlightControl, VirtualStickFlightControl.VirtualSticksIncrementListener {
    private static final String TAG = DeadReckoningFlightControl.class.getSimpleName();

    private FlightMode flightMode = null;
    private boolean rotationLock;
    private Coordinate position;
    private Coordinate direction;
    private Coordinate destination;
    private Timer endTimer;
    private TimerTask endTask;
    private DeadReckoningFlightControl.PositionListener positionListener = null;
    private VirtualStickFlightControl virtualSticks;

    public DeadReckoningFlightControl() {
        rotationLock = false;
        position = new Coordinate(0, 0, 0);
        direction = new Coordinate(0, 1, 0);
        endTimer = new Timer();
        endTask = null;
        virtualSticks = VirtualStickFlightControl.getInstance();
        virtualSticks.setListener(this);
    }

    public void setPositionListener(DeadReckoningFlightControl.PositionListener listener) {
        this.positionListener = listener;
    }

    public FlightMode getFlightMode() {
        return flightMode;
    }

    public void setFlightMode(FlightMode flightMode) {
        this.flightMode = flightMode;
    }

    public Coordinate getPosition() {
        return position;
    }

    public void goTo(Coordinate destination, @Nullable CommonCallbacks.CompletionCallback callback) {
//        if(destination.add(position).magnitude() < .01) {
//            if(callback != null)
//                callback.onResult(null);
//            return;
//        }
        this.destination = destination;
        if (flightMode == FlightMode.RELATIVE)
            relativeGoTo(destination, callback);
        else if (flightMode == FlightMode.ABSOLUTE)
            absoluteGoTo(destination, callback);
        else
            throw new IllegalStateException("FlightMode not set!");
    }

    public void rotateTo(float theta, @Nullable CommonCallbacks.CompletionCallback callback) {
        if (rotationLock) {
            if (callback != null)
                callback.onResult(null);
            return;
        }

        // Distinguish between absolute/relative
        if (flightMode == FlightMode.ABSOLUTE) {
            rotate(direction.angleBetween(theta), callback);
//            this.direction = this.direction.rotateByAngle(this.direction.angleBetween(theta));

        } else if (flightMode == FlightMode.RELATIVE) {
            rotate(theta, callback);
//            this.direction  = this.direction.rotateByAngle(theta);
        } else
            throw new IllegalStateException("FlightMode not set!");
    }

    public void relativeGoTo(Coordinate destination, @Nullable CommonCallbacks.CompletionCallback callback) {
        // Don't need to rotate.
        // Move the drone to the destination, which is easy since it's in the relative coordinates
        if (!rotationLock)
            rotateTo(position.add(destination.scale(-1)).angleFacing(), (error) -> {
                if(error != null)
                    move(destination, callback);
            });
        else
            move(destination, callback);

        //What is hard is figuring out where the drone ended up in the absolute coordinates

        // Using a unit vector perpendicular to the direction vector
//        Coordinate perpendicular = direction.perpendicularUnit();

        // We make the movement in the drones relative coordinate system
        // don't need to inject movement anymore
//        position = position.add(perpendicular.scale(destination.getX())
//                     .add(direction.scale(direction.getY())));
    }

    public void absoluteGoTo(Coordinate destination, @Nullable CommonCallbacks.CompletionCallback callback) {
        // If rotations not locked, then rotate so that you're facing the right way, then do a
        // normal relative goTo.
        Coordinate movement = destination.add(position.scale(-1));
//        if(!rotationLock)
//            rotateTo(movement.angleFacing(), (error) -> move(movement.inBasis(direction.perpendicularUnit(), direction), callback));
//        else
        move(movement.inBasis(direction.perpendicularUnit(), direction), callback);

        // don't need to inject movement anymore
//        position = destination;
    }

    public boolean isInFlight() {
        return virtualSticks.isInFlight();
    }

    public void halt() {
        virtualSticks.setCallbackFail(null);
        virtualSticks.halt();
        virtualSticks.disable();
        if (endTimer != null) {
            endTimer.cancel();
            endTimer.purge();
            endTimer = null;
            endTask = null;
        }
    }

    public void move(Coordinate movement, @Nullable CommonCallbacks.CompletionCallback callback) {
        move(movement, FlightCoordinateSystem.BODY, callback);
    }

    public void move(Coordinate movement, FlightCoordinateSystem flightCoordinateSystem, @Nullable CommonCallbacks.CompletionCallback callback) {
        virtualSticks.setRollPitchCoordinateSystem(flightCoordinateSystem);
        if (movement.magnitude() < .1) {
            if (callback != null)
                callback.onResult(null);
            return;
        }
        startFlight(movement, 0, callback);
    }

    public void rotate(float theta, @Nullable CommonCallbacks.CompletionCallback callback) {
        Log.v(TAG, "Starting flight with theta = " + String.valueOf(theta));
        if(theta == 0) {
            if (callback != null)
                callback.onResult(null);
            return;
        }
        startFlight(new Coordinate(0, 0, 0), theta, callback);
    }

    private void startFlight(Coordinate movement, float theta, @Nullable CommonCallbacks.CompletionCallback callback) {
        // if there's already a flight going, we want that to stop
        halt();

        // prep sticks for inputs
        virtualSticks.enable();
        virtualSticks.setCallbackFail(callback);
        if(theta == 0 && movement.magnitude() == 0) {
            if (callback != null)
                callback.onResult(null);
            return;
        }

        long duration;
        if (theta == 0) {
            duration = (long) (1000 * movement.magnitude() / virtualSticks.getSpeed());
            virtualSticks.setDirection(movement);
        } else if(movement.magnitude() == 0){
            duration = (long) (1000 * Math.abs(theta) / virtualSticks.getAngularVelocity());
            Log.v(TAG,"Duration = " + String.valueOf(duration));
            virtualSticks.setYaw(theta > 0);
        } else {
            // rotating and moving... not currently handled.
            throw new IllegalArgumentException("Simultaneous rotation and movement not supported");
        }

        // schedule task to halt flight.
        endTimer = new Timer();
        endTask = new TimerTask() {
            @Override
            public void run() {
                halt();
                Log.v(TAG, "End timer executed");
                if (callback != null)
                    callback.onResult(null);
            }
        };

        endTimer.schedule(endTask, duration);
    }

    interface PositionListener {
        public void updatePosition(Coordinate position);
    }

    @Override
    public void increment(Coordinate positionDelta, float rotationDelta) {
        // What follows is complicated math stuff

        if(rotationDelta == 0)
            position = position.add(positionDelta);
        else {
            direction = direction.rotateByAngle(rotationDelta);

            // for our team, we probably won't use both rotation and movement simultaneously
            // but for the future teams who may want to (based on camera input..), this will be useful
            if(positionDelta.xyMagnitude() > 0){

                // given a particular linear velocity, and angular velocity, the motion of
                // the object can be described by the object rotating about a point at a certain
                // angular velocity and radius, we have angularVel*r = linearVel, so...
                // r = linearVel/angularVel = (linearVel*time)/(angularVel*time)
                //                          = displacement/theta

                // an isosceles triangle can be made from the current position,
                // the center of the circle, and the new position.
                // Two sides will be the radius, and the third will be what we need to
                // scale our position vector by, after rotating it by the rotationDelta.
                // the angle between the radii will be the rotation delta. (theta)

                // length = r*sin(theta)/sin(90 - theta/2)
                //        = 2*r*sin(theta/2) -> small angle approximation -> r*theta
                //          (don't approximate if it turns out that theta/2 is bigger than ~10 deg)

                // turns out, r*theta = magnitude of movement

                // if you wanted to do this more accurately, use these to get l
                // float r = positionDelta.xyMagnitude()/rotationDelta;
                // float l = 2*r*(float)Math.sin(Math.PI*rotationDelta/360);

                float l = positionDelta.xyMagnitude();

                position = position
                        .add(positionDelta
                                .inBasis(direction.perpendicularUnit(), direction)
                                .xyUnit().scale(l)
                                // need to consider how rotation works after the basis itself rotated
                                .rotateByAngle(-rotationDelta))
                        // z should be the same
                        .add(new Coordinate(0,0,positionDelta.getZ()));
            }
        }

        if (positionListener != null)
            positionListener.updatePosition(position);
    }
}
