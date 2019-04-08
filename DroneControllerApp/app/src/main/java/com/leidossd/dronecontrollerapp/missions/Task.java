package com.leidossd.dronecontrollerapp.missions;

import android.os.Parcel;
import android.os.Parcelable;

abstract public class Task implements Parcelable {
    String title;
    TaskState currentState;
    StatusUpdateListener listener;

    // To add a new task type you'll need to add it to the enum, and the switch statement below
    // NOTE: DO NOT ADD MISSION TYPES HERE, THEY BELONG IN THE MISSION ABSTRACT CLASS

    abstract void start();
    abstract void stop();

    Task(String title) {
        this.title = title;
        this.currentState = TaskState.NOT_READY;
    }

    public String getTitle() {
        return title;
    }

    String getStatus() {
        return currentState.toString();
    }

    void setListener(StatusUpdateListener listener){
        this.listener = listener;
    }

    void clearListener(){
        this.listener = null;
    }

    public interface StatusUpdateListener {
        void statusUpdate(TaskState newStatus, String message);
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) { }

    public enum TaskState {
        NOT_READY,
        READY,
        RUNNING,
        COMPLETED,
        FAILED
    }
}
