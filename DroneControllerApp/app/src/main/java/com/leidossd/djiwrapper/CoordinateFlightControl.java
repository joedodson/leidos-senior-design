package com.leidossd.djiwrapper;

// Gives an interface to tell the drone where to go autonomously. GPS functionality can go through
// this once implemented, and missions will work the same.
public interface CoordinateFlightControl {

    enum FlightMode {ABSOLUTE, RELATIVE};

    FlightMode getFlightMode();
    void setFlightMode(FlightMode flightMode);
    void goTo(Coordinate destination); // add callback to report when done
    void rotateTo(float theta); // add callback to report when done
    boolean isInFlight();
    void halt();
    // need to do a kill/halt flight function
}
