package com.leidossd.dronecontrollerapp.missions;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

abstract public class Task implements Parcelable {
    String title;
    TaskState currentState;
    ListenerList listeners;

    // To add a new task type you'll need to add it to the enum, and the switch statement below
    // NOTE: DO NOT ADD MISSION TYPES HERE, THEY BELONG IN THE MISSION ABSTRACT CLASS

    abstract void start();

    abstract void stop();

    Task(String title) {
        this.title = title;
        this.currentState = TaskState.NOT_READY;
        this.listeners = new ListenerList();
    }

    public String getTitle() {
        return title;
    }

    public void addListener(StatusUpdateListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(StatusUpdateListener listener) {
        listeners.remove(listener);
    }

    protected void clearListener() {
        this.listeners.clear();
    }

    public TaskState getCurrentState() {
        return currentState;
    }

    public interface StatusUpdateListener {
        void statusUpdate(TaskState newStatus, String message);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public enum TaskState {
        NOT_READY,
        READY,
        RUNNING,
        COMPLETED,
        FAILED
    }

    protected class ListenerList {
        ArrayList<StatusUpdateListener> listeners;

        ListenerList() {
            this(new ArrayList<>());
        }

        ListenerList(ArrayList<StatusUpdateListener> listeners) {
            this.listeners = listeners;
        }

        public void statusUpdate(TaskState newStatus, String message) {
            for (StatusUpdateListener listener : listeners) {
                listener.statusUpdate(newStatus, message);
            }
        }

        public void add(StatusUpdateListener listener) {
            listeners.add(listener);
        }

        public void remove(StatusUpdateListener listener) {
            listeners.remove(listener);
        }

        public void clear(){
            listeners.clear();
        }
    }
}
