package com.leidossd.dronecontrollerapp.missions;

import android.os.Parcel;
import android.os.Parcelable;

import dji.sdk.sdkmanager.DJISDKManager;

public class StartRecordingTask extends Task {

    StartRecordingTask() {
        super("Start Recording");
        currentState = TaskState.READY;
    }

    static public StartRecordingTask create(Parcel in) {
        return new StartRecordingTask();
    }

    void start() {
        // Point camera down at an angle
//        DJISDKManager.getInstance().getProduct().getGimbal().
        DJISDKManager.getInstance().getProduct().getCamera().startRecordVideo((error) -> {
            if (error != null)
                listener.statusUpdate(TaskState.FAILED, error.getDescription());
            else
                listener.statusUpdate(TaskState.COMPLETED, "Started Recording...");
        });
    }

    void stop() {
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public StartRecordingTask createFromParcel(Parcel in) {
            return new StartRecordingTask();

        }

        public StartRecordingTask[] newArray(int size) {
            return new StartRecordingTask[size];
        }
    };
}
