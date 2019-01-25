package com.leidossd.djiwrapper;

interface CoordinateFlightControl {

    enum FlightMode {ABSOLUTE, RELATIVE};

    FlightMode getFlightMode();
    void setFlightMode(FlightMode flightMode);
    LowLevelFlightControl lowLevelFlightControl = VirtualStickFlightControl.getInstance();
    void goTo(Coordinate destination);
    void rotateTo(float theta);
}
