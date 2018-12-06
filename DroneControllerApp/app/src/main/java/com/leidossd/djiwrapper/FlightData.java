package com.leidossd.djiwrapper;

import java.lang.System;

public class FlightData {
    Coordinate start;
    Coordinate destination;

    long startTime;
    long endTime;

    public FlightData(Coordinate start, Coordinate destination, double flightTime){
        this.start = start;
        this.destination = destination;
        this.startTime = System.currentTimeMillis();
        this.endTime = System.currentTimeMillis() + (long) (flightTime*1000);
    }
    // Just takes the flight length and how long it's been since the flight started
    public Coordinate currentPosition(){
        float percent = ((float) ((System.currentTimeMillis() - startTime))/(endTime - startTime));
        return start.add(destination.add(start.scale(-1)).scale(percent));
    }
}
