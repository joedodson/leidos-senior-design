package com.leidossd.djiwrapper;

import java.lang.System;

public class FlightData {
    Coordinate start;
    Coordinate destination;

    long startTime;
    long endTime;


    public Coordinate currentPosition(){
        float percent = ((float) ((System.nanoTime() - startTime))/(endTime - startTime));
        return start.add(destination.add(start.scale(-1)).scale(percent));
    }
}
