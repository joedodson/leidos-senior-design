package com.leidossd.dronecontrollerapp.missions;

import android.os.Parcel;

import com.leidossd.djiwrapper.Coordinate;
import com.leidossd.djiwrapper.CoordinateFlightControl;
import com.leidossd.djiwrapper.FlightControllerWrapper;

public class FlightTask extends Task {
    private Coordinate destination;

    FlightTask(Coordinate destination){
        super("Fly to " + destination);
        this.destination = destination;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString("FLIGHT_TASK");
        dest.writeTypedObject(destination, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    void start(){
        FlightControllerWrapper.getInstance()
                .setFlightMode(CoordinateFlightControl.FlightMode.ABSOLUTE);
        // when i implement callbacks in the flightcontroller, I'll need to inject the status update
        FlightControllerWrapper.getInstance().gotoAbsoluteXYZ(this.destination, (error) -> {
            if(error != null)
                listener.statusUpdate(TaskState.FAILED, "ERROR IN gotoXYZ! " + error);
            currentState = TaskState.COMPLETED;
            listener.statusUpdate(currentState, title + " Completed");
        });
    }

    @Override
    void write(Parcel out){
        out.writeString("FLIGHT_TASK");
        out.writeString(title);
    }

    public static FlightTask create(Parcel in){
        return new FlightTask(in.readTypedObject(Coordinate.CREATOR));
    }

    @Override
    void stop(){
        FlightControllerWrapper.getInstance().haltFlight();
    }
}
