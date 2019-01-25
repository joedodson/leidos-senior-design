package com.leidossd.djiwrapper;


public class DeadReckoningFlightControl implements CoordinateFlightControl {
    FlightMode flightMode;
    boolean rotationLock;
    Coordinate position;
    Coordinate direction;

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

    public void goTo(Coordinate destination){
        if(flightMode == FlightMode.RELATIVE)
            relativeGoTo(destination);
        else if(flightMode == FlightMode.ABSOLUTE)
            absoluteGoTo(destination);
    }

    public void rotateTo(float theta){
        if(rotationLock)
            return;
    }

    void relativeGoTo(Coordinate destination){
        // Move the drone to the destination, which is easy since it's in the relative coordinates
        lowLevelFlightControl.move(destination);

        //What is hard is figuring out where the drone ended up in the absolute coordinates

        // Using a unit vector perpendicular to the direction vector
        Coordinate perpendicular = new Coordinate(1, -direction.getX()/direction.getY(), 0).unit();

        // We make the movement in the drones relative coordinate system
        position = position.add(perpendicular.scale(destination.getX())
                     .add(direction.scale(direction.getY())));

    }

    void absoluteGoTo(Coordinate destination){

    }

}
