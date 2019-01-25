package com.leidossd.djiwrapper;

// Gives an interface to tell the drone where to go autonomously. GPS functionality can go through
// this once implemented, and missions will work the same.
interface CoordinateFlightControl {

    enum FlightMode {ABSOLUTE, RELATIVE};

    FlightMode getFlightMode();
    void setFlightMode(FlightMode flightMode);
    LowLevelFlightControl lowLevelFlightControl = VirtualStickFlightControl.getInstance();
    void goTo(Coordinate destination);
    void rotateTo(float theta);
    // need to do a kill/halt flight function
}
