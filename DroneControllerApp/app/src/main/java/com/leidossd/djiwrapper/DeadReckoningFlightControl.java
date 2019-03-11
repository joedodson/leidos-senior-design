package com.leidossd.djiwrapper;


import android.support.annotation.Nullable;

import dji.common.util.CommonCallbacks;

public class DeadReckoningFlightControl implements CoordinateFlightControl {
    private FlightMode flightMode = null;
    private boolean rotationLock;
    private Coordinate position;
    private Coordinate direction;
    private VirtualStickFlightControl lowLevelFlightControl = VirtualStickFlightControl.getInstance();

    public DeadReckoningFlightControl(){
        rotationLock = true;
        position = new Coordinate(0,0,0);
        direction = new Coordinate(0,1,0);
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
            lowLevelFlightControl.rotate(direction.angleBetween(theta), callback);
            this.direction = this.direction.rotateByAngle(this.direction.angleBetween(theta));

        } else if(flightMode == FlightMode.RELATIVE){
            lowLevelFlightControl.rotate(theta, callback);
            this.direction  = this.direction.rotateByAngle(theta);
        }
        else
            throw new IllegalStateException("FlightMode not set!");
    }

    public void relativeGoTo(Coordinate destination, @Nullable CommonCallbacks.CompletionCallback callback){
        // Don't need to rotate.
        // Move the drone to the destination, which is easy since it's in the relative coordinates
        lowLevelFlightControl.move(destination, callback);

        //What is hard is figuring out where the drone ended up in the absolute coordinates

        // Using a unit vector perpendicular to the direction vector
        Coordinate perpendicular = direction.perpendicularUnit();

        // We make the movement in the drones relative coordinate system
        position = position.add(perpendicular.scale(destination.getX())
                     .add(direction.scale(direction.getY())));
    }

    public void absoluteGoTo(Coordinate destination, @Nullable CommonCallbacks.CompletionCallback callback){
        // If rotations not locked, then rotate so that you're facing the right way, then do a
        // normal relative goTo.
        Coordinate movement = destination.add(position.scale(-1));
        lowLevelFlightControl.move(movement.inBasis(direction.perpendicularUnit(), direction), callback);
        position = destination;
    }

//    public void incrementPosition(Coordinate increment){
//        position = position.add(increment.inBasis(direction, direction.perpendicularUnit()));
//    }

    public boolean isInFlight(){
        return lowLevelFlightControl.isInFlight();
    }

    public void halt(){
        lowLevelFlightControl.halt();
    }
}
