package com.leidossd.dronecontrollerapp.missions;

import android.os.Parcel;

import com.leidossd.djiwrapper.FlightControllerWrapper;

public class RotationTask extends Task {
    private float angle;
    Creator<RotationTask> CREATOR;

    RotationTask(float angle){
        super("Rotate to %f" + Float.toString(angle));
        this.angle = angle;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    void start(){

    }

    void stop(){

    }



}
