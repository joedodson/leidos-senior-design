package com.leidossd.dronecontrollerapp.missions;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.Locale;

public class WaitTask extends Task {
    private long milliseconds;

    WaitTask(long milliseconds) {
        super(String.format(Locale.getDefault(), "Waiting for %dms", milliseconds));
        this.milliseconds = milliseconds;
        currentState = TaskState.READY;
    }

    static public WaitTask create(Parcel in) {
        return new WaitTask(in.readLong());
    }

    void start() {
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                currentState = TaskState.COMPLETED;
//                listener.statusUpdate(TaskState.COMPLETED, "Wait finished");
//            }
//        }, milliseconds);
        try {
            Thread.sleep(milliseconds);
        } catch(InterruptedException e){
            Log.e("TAG", e.getLocalizedMessage());
        }
        currentState = TaskState.COMPLETED;
        listener.statusUpdate(currentState, "Wait finished");
    }

    void stop() {

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
