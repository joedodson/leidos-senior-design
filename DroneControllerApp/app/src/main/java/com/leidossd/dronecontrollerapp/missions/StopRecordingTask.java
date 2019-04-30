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
                listeners.statusUpdate(TaskState.FAILED, error.getDescription());
            else
                listeners.statusUpdate(TaskState.COMPLETED, "Stopped Recording...");
        });
    }

    void stop() {
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public StopRecordingTask createFromParcel(Parcel in) {
            return new StopRecordingTask();

        }

        public StopRecordingTask[] newArray(int size) {
            return new StopRecordingTask[size];
        }
    };
}
