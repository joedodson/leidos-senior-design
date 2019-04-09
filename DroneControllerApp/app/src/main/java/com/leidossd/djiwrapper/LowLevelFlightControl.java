package com.leidossd.djiwrapper;

import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKManager;

// A simple interface so that if we find another way to implement this functionality we can
// easily extend it and try it out, and coordinate flight will still work.
interface LowLevelFlightControl {
    FlightController flightController = ((Aircraft) DJISDKManager.getInstance().
            getProduct()).getFlightController();

    boolean isInFlight();

    void move(Coordinate movement);

    void rotate(float theta);

    void halt();

    // need to do a kill/halt function

    // potentially roll/pitch/yaw accessors, might be useful
    // to see what is being sent to the virtual sticks.
}