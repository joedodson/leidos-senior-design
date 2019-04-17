package com.leidossd.dronecontrollerapp.missions;

import android.graphics.Camera;
import android.os.Parcel;
import android.os.Parcelable;

import dji.common.camera.SettingsDefinitions;
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
        DJISDKManager.getInstance().getProduct().getCamera().setStorageLocation(SettingsDefinitions.StorageLocation.SDCARD, (error0) -> {
            if (error0 == null)
                DJISDKManager.getInstance().getProduct().getCamera().setMode(SettingsDefinitions.CameraMode.RECORD_VIDEO, (error) -> {
                    if (error == null) {
                        DJISDKManager.getInstance().getProduct().getCamera().startRecordVideo((error1) -> {
                            if (error1 != null)
                                listener.statusUpdate(TaskState.FAILED, "Error starting recording: " + error1.getDescription());
                            else
                                listener.statusUpdate(TaskState.COMPLETED, "Started Recording...");
                        });
                    } else
                        listener.statusUpdate(TaskState.FAILED, "Error while setting camera mode to record: " + error.getDescription());
                });
            else
                listener.statusUpdate(TaskState.FAILED, "Error while setting storage location: " + error0.getDescription());
        });
//        DJISDKManager.getInstance().getProduct().getCamera().startRecordVideo((error) -> {
//            if (error != null)
//                listener.statusUpdate(TaskState.FAILED, error.getDescription());
//            else
//                listener.statusUpdate(TaskState.COMPLETED, "Started Recording...");
//        });
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
