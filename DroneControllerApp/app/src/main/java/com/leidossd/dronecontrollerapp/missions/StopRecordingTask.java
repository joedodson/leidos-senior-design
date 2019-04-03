package com.leidossd.dronecontrollerapp.missions;

        import android.os.Parcel;

        import dji.sdk.sdkmanager.DJISDKManager;

public class StopRecordingTask extends Task {
    StopRecordingTask() {
        super("Stop Recording");
        currentState = TaskState.READY;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString("STOP_RECORDING_TASK");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    static public StopRecordingTask create(Parcel in){
        return new StopRecordingTask();
    }

    @Override
    void write(Parcel out){
        out.writeString("STOP_RECORDING_TASK");
    }

    void start(){
        // Point camera down at an angle
//        DJISDKManager.getInstance().getProduct().getGimbal().
        DJISDKManager.getInstance().getProduct().getCamera().stopRecordVideo((error) -> {
            if(error != null)
                listener.statusUpdate(TaskState.FAILED, error.getDescription());
            else
                listener.statusUpdate(TaskState.COMPLETED, "Stoped Recording...");
        });
    }

    void stop(){

    }
}
