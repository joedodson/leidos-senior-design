package com.leidossd.dronecontrollerapp.missions;

import android.os.Parcel;

import dji.sdk.sdkmanager.DJISDKManager;

public class StartRecordingTask extends Task {


    StartRecordingTask() {
        super("Start Recording");
        currentState = TaskState.READY;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString("START_RECORDING_TASK");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    static public StartRecordingTask create(Parcel in){
        return new StartRecordingTask();
    }

    @Override
    void write(Parcel out){
        out.writeString("START_RECORDING_TASK");
    }

    void start(){
        // Point camera down at an angle
//        DJISDKManager.getInstance().getProduct().getGimbal().
        DJISDKManager.getInstance().getProduct().getCamera().startRecordVideo((error) -> {
            if(error != null)
                listener.statusUpdate(TaskState.FAILED, error.getDescription());
            else
                listener.statusUpdate(TaskState.COMPLETED, "Started Recording...");
        });
    }

    void stop(){

    }
}
