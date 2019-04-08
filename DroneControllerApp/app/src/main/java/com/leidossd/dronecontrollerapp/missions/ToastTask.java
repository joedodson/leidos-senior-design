package com.leidossd.dronecontrollerapp.missions;

import android.os.Parcel;

import static com.leidossd.dronecontrollerapp.MainApplication.showToast;

public class ToastTask extends Task {
    String toast;

    public static ToastTask create(Parcel in){
        return new ToastTask(in.readString());
    }

    @Override
    public void write(Parcel out){
        out.writeString("TOAST_TASK");
        out.writeString(toast);
    }

    ToastTask(String toast) {
        super(String.format("Showing Toast \"%s\"", toast));
        this.toast = toast;
    }

    void start(){
        currentState = TaskState.RUNNING;
        listener.statusUpdate(currentState, String.format("Task \"%s\" started.", title));
        showToast(toast);
        currentState = TaskState.COMPLETED;
        listener.statusUpdate(currentState, String.format("Task \"%s\" finished.", title));
    }

    void stop(){

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        write(dest);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
