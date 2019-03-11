package com.leidossd.dronecontrollerapp.missions;

import android.os.Parcel;

import static com.leidossd.dronecontrollerapp.MainApplication.showToast;

public class ToastTask extends Task {
    String toast;
    Creator<WaitTask> CREATOR;

    ToastTask(String toast) {
        super(String.format("Showing Toast \"%s\"", toast));
        this.toast = toast;
    }

    void start(){
        currentState = TaskState.RUNNING;
        listener.statusUpdate(currentState, String.format("Task \"%s\" started.", title));
        showToast(toast);
        currentState = TaskState.COMPLETED;
    }

    void stop(){

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    @Override
    public int describeContents() {
        return 0;
    }
}
