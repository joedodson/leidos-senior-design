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

    @Override
    void write(Parcel out){

    }

    static public RotationTask create(Parcel in){
        return null;
    }

    void start(){

    }

    void stop(){

    }



}
