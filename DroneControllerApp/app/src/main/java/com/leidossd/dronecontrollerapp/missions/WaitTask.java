package com.leidossd.dronecontrollerapp.missions;

import android.os.Handler;
import android.os.Parcel;

public class WaitTask extends Task {
    private long milliseconds;

    WaitTask(long milliseconds) {
        super(String.format("Waiting for %dms", milliseconds));
        this.milliseconds = milliseconds;
    }

    public static final Creator<WaitTask> CREATOR = new Creator<WaitTask>(){
        @Override
        public WaitTask createFromParcel(Parcel source) {
            return new WaitTask(source);
        }

        @Override
        public WaitTask[] newArray(int size) {
            return new WaitTask[size];
        }
    };

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(title);
        out.writeLong(milliseconds);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private WaitTask(Parcel in){
        super(in.readString());
        this.milliseconds = in.readLong();
    }

    void start(){
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            currentState = TaskState.COMPLETED;
            listener.statusUpdate(currentState, "Wait finished");
        }, milliseconds);
    }

    void stop(){

    }
}
