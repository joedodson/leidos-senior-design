package com.leidossd.dronecontrollerapp.missions;

import android.os.Parcel;
import android.os.Parcelable;

import dji.sdk.sdkmanager.DJISDKManager;

public class StopRecordingTask extends Task {
    StopRecordingTask() {
        super("Stop Recording");
        currentState = TaskState.READY;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    static public StopRecordingTask create(Parcel in) {
        return new StopRecordingTask();
    }

    void start() {
        // Point camera down at an angle
//        DJISDKManager.getInstance().getProduct().getGimbal().
        DJISDKManager.getInstance().getProduct().getCamera().stopRecordVideo((error) -> {
            if (error != null)
                listener.statusUpdate(TaskState.FAILED, error.getDescription());
            else
                listener.statusUpdate(TaskState.COMPLETED, "Stoped Recording...");
        });
    }

    void stop() {
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public TakeOffTask createFromParcel(Parcel in) {
            return new TakeOffTask();

        }

        public TakeOffTask[] newArray(int size) {
            return new TakeOffTask[size];
        }
    };
}
