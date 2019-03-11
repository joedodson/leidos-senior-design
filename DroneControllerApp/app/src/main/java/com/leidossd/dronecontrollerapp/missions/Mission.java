package com.leidossd.dronecontrollerapp.missions;

import android.os.Parcel;
import android.os.Parcelable;


abstract public class Mission extends Task implements Task.StatusUpdateListener {
    private Task currentTask;
    private int currentTaskId = 0;
    Iterable<Task> taskIterable;

    Mission(String title) {
        super(title);
    }

    Mission(String title, Iterable<Task> taskIterable){
        super(title);
        this.taskIterable = taskIterable;
    }

    enum MissionType {
        SPECIFIC_MISSION,
    }

    public static final Creator<Mission> CREATOR = new Creator<Mission>(){
        @Override
        public Mission createFromParcel(Parcel source) {
            switch(MissionType.valueOf(source.readString()))
            return null;
        }

        @Override
        public Mission[] newArray(int size) {
            return new Mission[0];
        }
    }

    private void nextTask(){
        if(!taskIterable.iterator().hasNext()) {
            currentState = TaskState.COMPLETED;
            listener.statusUpdate(currentState, "Mission finished.");
            return;
        }

        currentTask.clearListener();

        currentTaskId += 1;
        currentTask = taskIterable.iterator().next();

        currentTask.setListener(this);
        currentTask.start();
    }

    void start(){
        if(currentState != TaskState.READY)
            return;

        currentState = TaskState.RUNNING;
        listener.statusUpdate(currentState, String.format("Mission \"%s\" started", title));
        nextTask();
    }

    void stop(){
        currentTask.stop();
        currentState = TaskState.COMPLETED;
        listener.statusUpdate(currentState, String.format("Mission \"%s\" stopped", title));
    }

    @Override
    public void statusUpdate(Task.TaskState state, String message){
        // Tasks shouldn't report ready/notready/running
        switch(state){
            case COMPLETED:
                nextTask();
                break;
            case FAILED:
                listener.statusUpdate(state, message);
                break;
            default:
                break;
        }
    }
}
