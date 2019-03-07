package com.leidossd.dronecontrollerapp.missions;

import com.leidossd.djiwrapper.Coordinate;
import com.leidossd.djiwrapper.CoordinateFlightControl;
import com.leidossd.djiwrapper.FlightControllerWrapper;

public class FlightTask extends Task {
    private Coordinate destination;

    FlightTask(Coordinate destination){
        this.destination = destination;
    }

    @Override
    void exec(){
        FlightControllerWrapper.getInstance()
                .setFlightMode(CoordinateFlightControl.FlightMode.ABSOLUTE);
        FlightControllerWrapper.getInstance().gotoXYZ(this.destination);
    }

    @Override
    String getDescription(){
        return "Fly to " + this.destination;
    }
}
