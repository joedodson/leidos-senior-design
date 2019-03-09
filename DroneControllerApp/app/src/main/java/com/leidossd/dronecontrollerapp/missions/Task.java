package com.leidossd.dronecontrollerapp.missions;

import android.os.Parcel;
import android.os.Parcelable;

abstract public class Task implements Parcelable {
    String title;
    TaskState currentState;
    StatusUpdateListener listener;

    abstract void start();
    abstract void stop();

    Task(String title) {
        this.title = title;
        this.currentState = TaskState.NOT_READY;
    }

    Task(Parcel in){}

    String getTitle() {
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

    interface StatusUpdateListener {
        void statusUpdate(TaskState newStatus);
    }

    public enum TaskState {
        NOT_READY,
        READY,
        RUNNING,
        COMPLETED,
        FAILED
    }
}
