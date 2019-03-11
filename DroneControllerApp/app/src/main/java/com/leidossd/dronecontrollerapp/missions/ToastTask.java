package com.leidossd.dronecontrollerapp.missions;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import static com.leidossd.dronecontrollerapp.MainApplication.showToast;

public class ToastTask extends Task {
    String toast;
//    public static final Creator<ToastTask> CREATOR = new Creator<ToastTask>(){
//        @Override
//        public ToastTask createFromParcel(Parcel source) {
//            return new ToastTask(source);
////            Bundle bundle = source.readBundle();
////            String tst = bundle.getString("toast");
////            return new ToastTask(tst);
//        }
//
//        @Override
//        public ToastTask[] newArray(int size) {
//            return new ToastTask[size];
//        }
//    };

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

    ToastTask(Parcel in){
        this(in.readString());
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
        write(dest);
//        dest.writeString("TOAST_TASK");
//        dest.writeString(toast);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
