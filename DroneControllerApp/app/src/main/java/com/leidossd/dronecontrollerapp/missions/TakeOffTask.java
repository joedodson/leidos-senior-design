package com.leidossd.dronecontrollerapp.missions;

import android.os.Parcel;

import com.leidossd.djiwrapper.FlightControllerWrapper;


public class TakeOffTask extends Task {
    TakeOffTask(){
        super("Taking Off...");
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString("TAKEOFF_TASK");
        dest.writeString("HAHA");
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
    void write(Parcel out){
        out.writeString("TAKEOFF_TASK");
    }

    public static TakeOffTask create(Parcel in){
        in.readString();
        return new TakeOffTask();
    }

    @Override
    void stop(){
    }
}

