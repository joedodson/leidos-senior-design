package com.leidossd.dronecontrollerapp.missions;

import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;

public class WaitTask extends Task {
    private long milliseconds;

    WaitTask(long milliseconds) {
        super(String.format("Waiting for %dms", milliseconds));
        this.milliseconds = milliseconds;
        currentState = TaskState.READY;
    }

    static public WaitTask create(Parcel in){
        return new WaitTask(in.readLong());
    }

    void start(){
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            currentState = TaskState.COMPLETED;
            listener.statusUpdate(TaskState.COMPLETED, "Wait finished");
        }, milliseconds);
    }

    void stop(){

    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public WaitTask createFromParcel(Parcel in) {
            return new WaitTask(in.readLong());

        }

        public WaitTask[] newArray(int size) {
            return new WaitTask[size];
        }
    };

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(milliseconds);
    }
}
