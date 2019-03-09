package com.leidossd.dronecontrollerapp.missions;

import android.os.Parcel;

import com.leidossd.djiwrapper.Coordinate;
import com.leidossd.djiwrapper.CoordinateFlightControl;
import com.leidossd.djiwrapper.FlightControllerWrapper;

public class FlightTask extends Task {
    private Coordinate destination;
    Creator<FlightTask> CREATOR;

    FlightTask(Coordinate destination){
        super("Fly to " + destination);
        this.destination = destination;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    void start(){
        FlightControllerWrapper.getInstance()
                .setFlightMode(CoordinateFlightControl.FlightMode.ABSOLUTE);
        FlightControllerWrapper.getInstance().gotoXYZ(this.destination);
    }

    @Override
    void stop(){
        FlightControllerWrapper.getInstance().haltFlight();
    }
}
