package com.leidossd.djiwrapper;

import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKManager;

// A simple interface so that if we find another way to implement this functionality we can
// easily extend it and try it out
interface LowLevelFlightControl {
    FlightController flightController = ((Aircraft) DJISDKManager.getInstance().
                                       getProduct()).getFlightController();

    static LowLevelFlightControl getInstance();
    boolean isInFlight();
    void move(Coordinate movement);
    void rotate(float theta);
}