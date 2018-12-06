package com.leidossd.djiwrapper;

import java.lang.System;

public class FlightData {
    Coordinate start;
    Coordinate destination;

    long startTime;
    long endTime;

    // Just takes the flight length and how long it's been since the flight started
    public Coordinate currentPosition(){
        float percent = ((float) ((System.nanoTime() - startTime))/(endTime - startTime));
        return start.add(destination.add(start.scale(-1)).scale(percent));
    }
}
