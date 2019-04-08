package com.leidossd.dronecontrollerapp.missions;

import android.os.Parcel;
import android.os.Parcelable;

import com.leidossd.djiwrapper.FlightControllerWrapper;

public class LandingTask extends Task {
    LandingTask(){
        super("Landing...");
    }

    @Override
    void start(){
        if(!FlightControllerWrapper.getInstance().isAirborne())
            listener.statusUpdate(TaskState.COMPLETED, title + " completed");
        FlightControllerWrapper.getInstance()
                .startLanding((error) -> {
                    if(error != null)
                        listener.statusUpdate(TaskState.FAILED, "ERROR IN Landing!");
                    currentState = TaskState.COMPLETED;
                    listener.statusUpdate(currentState, title + " completed");
                });
    }

    @Override
    void stop(){
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public LandingTask createFromParcel(Parcel in) {
            return new LandingTask();

        }

        public LandingTask[] newArray(int size) {
            return new LandingTask[size];
        }
    };
}
