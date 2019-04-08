package com.leidossd.dronecontrollerapp.missions;

import android.os.Parcel;
import android.os.Parcelable;

import com.leidossd.djiwrapper.Coordinate;
import com.leidossd.djiwrapper.CoordinateFlightControl;
import com.leidossd.djiwrapper.FlightControllerWrapper;

import static com.leidossd.dronecontrollerapp.MainApplication.showToast;

public class FlightTask extends Task {
    private Coordinate destination;

    FlightTask(Coordinate destination){
        super("Fly to " + destination);
        this.destination = destination;
    }

    @Override
    void start(){
        FlightControllerWrapper.getInstance()
                .setFlightMode(CoordinateFlightControl.FlightMode.ABSOLUTE);

        FlightControllerWrapper.getInstance().gotoXYZ(destination, (error) -> {
            if(error != null)
                listener.statusUpdate(TaskState.FAILED, "ERROR IN gotoXYZ! " + error);
            else {
                currentState = TaskState.COMPLETED;
                listener.statusUpdate(currentState, title + " Completed");
            }
        });
    }

    @Override
    void stop(){
        FlightControllerWrapper.getInstance().haltFlight();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public FlightTask createFromParcel(Parcel in) {
            float x = in.readFloat();
            float y = in.readFloat();
            float z = in.readFloat();

            return new FlightTask(new Coordinate(x,y,z));
        }

        public FlightTask[] newArray(int size) {
            return new FlightTask[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(destination.getX());
        dest.writeFloat(destination.getY());
        dest.writeFloat(destination.getZ());
    }
}
