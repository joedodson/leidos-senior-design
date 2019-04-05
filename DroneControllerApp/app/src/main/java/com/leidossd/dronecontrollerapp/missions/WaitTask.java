package com.leidossd.dronecontrollerapp.missions;

import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;

public class WaitTask extends Task {
    private long milliseconds;

    WaitTask(long milliseconds) {
        super(String.format("Waiting for %dms", milliseconds));
        this.milliseconds = milliseconds;
        currentState = TaskState.READY;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString("WAIT_TASK");
        out.writeLong(milliseconds);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    static public WaitTask create(Parcel in){
        return new WaitTask(in.readLong());
    }

    @Override
    void write(Parcel out){
        out.writeString("WAIT_TASK");
        out.writeLong(milliseconds);
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
}
