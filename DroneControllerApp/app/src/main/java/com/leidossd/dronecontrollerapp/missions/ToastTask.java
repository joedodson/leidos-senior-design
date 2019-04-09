package com.leidossd.dronecontrollerapp.missions;

import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;

import static com.leidossd.dronecontrollerapp.MainApplication.showToast;

public class ToastTask extends Task {
    String toast;

    ToastTask(String toast) {
        super(String.format("Showing Toast \"%s\"", toast));
        this.toast = toast;
    }

    void start() {
        Looper.prepare();
        currentState = TaskState.RUNNING;
        listener.statusUpdate(currentState, String.format("Task \"%s\" started.", title));
        showToast(toast);
        currentState = TaskState.COMPLETED;
        listener.statusUpdate(currentState, String.format("Task \"%s\" finished.", title));
        Looper.loop();
    }

    void stop() {

    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ToastTask createFromParcel(Parcel in) {
            return new ToastTask(in.readString());

        }

        public ToastTask[] newArray(int size) {
            return new ToastTask[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(toast);
    }
}
