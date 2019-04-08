package com.leidossd.dronecontrollerapp.missions;

import android.os.Parcel;
import android.os.Parcelable;

import com.leidossd.djiwrapper.FlightControllerWrapper;


public class TakeOffTask extends Task {
    TakeOffTask(){
        super("Taking Off...");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    void start(){
        if(FlightControllerWrapper.getInstance().isAirborne())
            listener.statusUpdate(TaskState.COMPLETED, title + " completed");
        FlightControllerWrapper.getInstance()
                .startTakeoff((error) -> {
                    if(error != null) {
                        if(FlightControllerWrapper.getInstance().compassHasError())
                            listener.statusUpdate(TaskState.FAILED, "Compass needs to be calibrated!");
                        else
                            listener.statusUpdate(TaskState.FAILED, error.toString());
                    }
                    else {
                        currentState = TaskState.COMPLETED;
                        listener.statusUpdate(currentState, title + " completed");
                    };
                });
    }

    @Override
    void stop(){ }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public TakeOffTask createFromParcel(Parcel in) {
            return new TakeOffTask();

        }

        public TakeOffTask[] newArray(int size) {
            return new TakeOffTask[size];
        }
    };
}

