package com.leidossd.dronecontrollerapp.missions;

import android.os.Parcel;

import com.leidossd.djiwrapper.FlightControllerWrapper;

public class LandingTask extends Task {
    LandingTask(){
        super("Landing...");
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString("LANDING_TASK");
        dest.writeString("HAHA");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    void start(){
        if(!FlightControllerWrapper.getInstance().isAirborne())
            listener.statusUpdate(TaskState.COMPLETED, title + " completed");
        FlightControllerWrapper.getInstance()
                .startLanding((error) -> {
                    if(error != null)
                        listener.statusUpdate(TaskState.FAILED, "ERROR IN Landing!");
                    else {
                        currentState = TaskState.COMPLETED;
                        listener.statusUpdate(currentState, title + " completed");
                    }
                });
    }

    @Override
    void write(Parcel out){
        out.writeString("LANDING_TASK");
    }

    public static LandingTask create(Parcel in){
        in.readString();
        return new LandingTask();
    }

    @Override
    void stop(){
    }
}
