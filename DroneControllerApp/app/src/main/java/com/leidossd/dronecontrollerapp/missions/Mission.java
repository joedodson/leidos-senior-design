package com.leidossd.dronecontrollerapp.missions;

import android.os.Parcel;
import android.os.Parcelable;


abstract public class Mission extends Task implements Task.StatusUpdateListener {
    private Task currentTask;
    private int currentTaskId = 0;
    private Iterable<Task> taskIterable;

    Mission(String title) {
        super(title);
    }

    Mission(String title, Iterable<Task> taskIterable){
        super(title);
        this.taskIterable = taskIterable;
    }

    Mission(Parcel in) {
        // get title out
        // get tasks out
        super(in.readString());
    }

    private void nextTask(){
        if(!taskIterable.iterator().hasNext()) {
            currentState = TaskState.COMPLETED;
            listener.statusUpdate(currentState);
            return;
        }

        currentTask.clearListener();

        currentTaskId += 1;
        currentTask = taskIterable.iterator().next();

        currentTask.setListener(this);
        currentTask.start();
    }

    void start(){
        if(currentState == TaskState.READY)
            nextTask();
    }

    void stop(){
        currentTask.stop();
    }

    @Override
    public void statusUpdate(Task.TaskState state){
        // Tasks shouldn't report ready/notready/running
        switch(state){
            case COMPLETED:
                nextTask();
                break;
            case FAILED:
                listener.statusUpdate(state);
                break;
            default:
                break;
        }
    }
}
