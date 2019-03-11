package com.leidossd.dronecontrollerapp.missions;

import android.os.Parcel;

import com.leidossd.djiwrapper.FlightControllerWrapper;

public class RotationTask extends Task {
    private float angle;

    RotationTask(float angle){
        super("Rotate to %f" + Float.toString(angle));
        this.angle = angle;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString("ROTATION_TASK");
        dest.writeFloat(angle);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    void write(Parcel out){

    }

    static public RotationTask create(Parcel in){
        return new RotationTask(in.readFloat());
    }

    void start(){
        FlightControllerWrapper.getInstance().rotateTo(angle, (error)->{
            if(error != null)
                listener.statusUpdate(TaskState.FAILED, "ROTATION FAILED!");
            currentState = TaskState.COMPLETED;
            listener.statusUpdate(currentState, "ROTATION SUCCESS");
        });

    }

    void stop(){

    }



}
