package com.leidossd.dronecontrollerapp.missions;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import static com.leidossd.dronecontrollerapp.MainApplication.showToast;

public class ToastTask extends Task {
    String toast;
    public static final Creator<ToastTask> CREATOR = new Creator<ToastTask>(){
        @Override
        public ToastTask createFromParcel(Parcel source) {
            Bundle bundle = source.readBundle();
            String tst = bundle.getString("toast");
            return new ToastTask(tst);
        }

        @Override
        public ToastTask[] newArray(int size) {
            return new ToastTask[size];
        }
    };

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
        Bundle bndl = new Bundle();
        bndl.putString("toast", toast);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
