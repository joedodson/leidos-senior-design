package com.leidossd.djiwrapper;


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

    public void goTo(Coordinate destination){
        if(flightMode == FlightMode.RELATIVE)
            relativeGoTo(destination);
        else if(flightMode == FlightMode.ABSOLUTE)
            absoluteGoTo(destination);
        else
            throw new IllegalStateException("FlightMode not set!");
    }

    public void rotateTo(float theta){
        if(rotationLock)
            return;

        // Distinguish between absolute/relative
        if(flightMode == FlightMode.ABSOLUTE){

        } else if(flightMode == FlightMode.RELATIVE){

        }
    }

    public void relativeGoTo(Coordinate destination){
        // Don't need to rotate.
        // Move the drone to the destination, which is easy since it's in the relative coordinates
        lowLevelFlightControl.move(destination);

        //What is hard is figuring out where the drone ended up in the absolute coordinates

        // Using a unit vector perpendicular to the direction vector
        Coordinate perpendicular = direction.perpendicularUnit();

        // We make the movement in the drones relative coordinate system
        position = position.add(perpendicular.scale(destination.getX())
                     .add(direction.scale(direction.getY())));
    }

    public void absoluteGoTo(Coordinate destination){
        // If rotations not locked, then rotate so that you're facing the right way, then do a
        // normal relative goTo.
        Coordinate movement = destination.add(position.scale(-1));
        lowLevelFlightControl.move(movement.inBasis(direction, direction.perpendicularUnit()));
        position = destination;
    }

    public boolean isInFlight(){
        return lowLevelFlightControl.isInFlight();
    }

    public void halt(){
        lowLevelFlightControl.halt();
    }
}
