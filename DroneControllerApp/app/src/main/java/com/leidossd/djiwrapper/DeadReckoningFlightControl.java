package com.leidossd.djiwrapper;


import android.os.Handler;
import android.support.annotation.Nullable;

import dji.common.flightcontroller.virtualstick.FlightCoordinateSystem;
import dji.common.util.CommonCallbacks;

public class DeadReckoningFlightControl implements CoordinateFlightControl, VirtualStickFlightControl.VirtualSticksIncrementListener {
    private FlightMode flightMode = null;
    private boolean rotationLock;
    private Coordinate position;
    private Coordinate direction;
    private Coordinate destination;
    private VirtualStickFlightControl virtualSticks = VirtualStickFlightControl.getInstance();

    public DeadReckoningFlightControl(){
        rotationLock = true;
        position = new Coordinate(0,0,0);
        direction = new Coordinate(0,1,0);
        virtualSticks.setListener(this);
    }

    public FlightMode getFlightMode(){
        return flightMode;
    }

    public void setFlightMode(FlightMode flightMode){
        this.flightMode = flightMode;
    }

    public Coordinate getPosition(){
        return position;
    }

    public void goTo(Coordinate destination, @Nullable CommonCallbacks.CompletionCallback callback){
        this.destination = destination;
        if(flightMode == FlightMode.RELATIVE)
            relativeGoTo(destination, callback);
        else if(flightMode == FlightMode.ABSOLUTE)
            absoluteGoTo(destination, callback);
        else
            throw new IllegalStateException("FlightMode not set!");
    }

    public void rotateTo(float theta, @Nullable CommonCallbacks.CompletionCallback callback){
        if(rotationLock)
            return;

        // Distinguish between absolute/relative
        if(flightMode == FlightMode.ABSOLUTE){
            rotate(direction.angleBetween(theta), callback);
            this.direction = this.direction.rotateByAngle(this.direction.angleBetween(theta));

        } else if(flightMode == FlightMode.RELATIVE){
            rotate(theta, callback);
            this.direction  = this.direction.rotateByAngle(theta);
        }
        else
            throw new IllegalStateException("FlightMode not set!");
    }

    public void relativeGoTo(Coordinate destination, @Nullable CommonCallbacks.CompletionCallback callback){
        // Don't need to rotate.
        // Move the drone to the destination, which is easy since it's in the relative coordinates
        if(!rotationLock)
            rotateTo(destination.angleFacing(), (error) -> move(destination, callback));
        else
            move(destination, callback);

        //What is hard is figuring out where the drone ended up in the absolute coordinates

        // Using a unit vector perpendicular to the direction vector
        Coordinate perpendicular = direction.perpendicularUnit();

        // We make the movement in the drones relative coordinate system
        // don't need to inject movement anymore
//        position = position.add(perpendicular.scale(destination.getX())
//                     .add(direction.scale(direction.getY())));
    }

    public void absoluteGoTo(Coordinate destination, @Nullable CommonCallbacks.CompletionCallback callback){
        // If rotations not locked, then rotate so that you're facing the right way, then do a
        // normal relative goTo.
        Coordinate movement = destination.add(position.scale(-1));
        if(!rotationLock)
            rotateTo(movement.angleFacing(), (error) -> move(movement.inBasis(direction.perpendicularUnit(), direction), callback));
        else
            move(movement.inBasis(direction.perpendicularUnit(), direction), callback);

        // don't need to inject movement anymore
//        position = destination;
    }

    public boolean isInFlight(){
        return virtualSticks.isInFlight();
    }

    public void halt(){
        virtualSticks.halt();
    }

    public void move(Coordinate movement, @Nullable CommonCallbacks.CompletionCallback callback){
       move(movement, FlightCoordinateSystem.BODY, callback);
    }

    public void move(Coordinate movement, FlightCoordinateSystem flightCoordinateSystem, @Nullable CommonCallbacks.CompletionCallback callback){
        virtualSticks.setRollPitchCoordinateSystem(flightCoordinateSystem);
        startFlight(movement, 0, callback);
    }

    public void rotate(float theta, @Nullable CommonCallbacks.CompletionCallback callback){
        startFlight(new Coordinate(0,0,0), theta, callback);
    }

    private void startFlight(Coordinate movement, float theta, @Nullable CommonCallbacks.CompletionCallback callback){
        // enable flight before starting. may break out into separate functions to enable/disable
        virtualSticks.enable();
        // changing these values changes the flight in real time
        long duration;
        if(theta == 0) {
            duration =(long) (1000*movement.magnitude()/virtualSticks.getSpeed());
            virtualSticks.setDirection(movement);
        }
        else {
            duration = (long) (theta/virtualSticks.getAngularVelocity());
            virtualSticks.setYaw(theta);
        }
        new Handler().postDelayed(()->{
            halt();
            if(callback != null)
                callback.onResult(null);
        },duration);
    }

    @Override
    public void increment(Coordinate positionDelta, float rotationDelta){
        // What follows is complicated math stuff

        // given a particular velocity, and angular velocity, the motion of the object can be
        // described by the object rotating about a point at a certain angular velocity and radius

        // an isosceles triangle can be made from the current position, the center of the circle,
        // and the new position. Two sides will be the radius, and the third will be what we need to
        // scale our vector by, after rotating it by the rotationDelta. the angle between the radii
        // will be the rotation delta.

        // the new direction is easy, and simplifying the expression for the length of the third leg
        // of the aformentioned triangle gives us the following.

        if(rotationDelta != 0) {
            direction = direction.rotateByAngle(rotationDelta);
            position = position.add(direction.rotateByAngle(rotationDelta)
                    .scale(positionDelta.magnitude() * (float) Math.sin(rotationDelta * Math.PI / 360)))
                    // of course, all of the math above was in the xy plane, the z value should be fine
                    .add(new Coordinate(0, 0, positionDelta.getZ()));
        }
        else {
            position = position.add(direction.scale(positionDelta.getY()))
                    .add(direction.perpendicularUnit().scale(positionDelta.getX()));
        }

    }
}
