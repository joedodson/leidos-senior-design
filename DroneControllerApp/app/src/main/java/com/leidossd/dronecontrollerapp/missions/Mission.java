package com.leidossd.dronecontrollerapp.missions;

import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;

import java.util.ArrayList;


abstract public class Mission extends Task implements Task.StatusUpdateListener {
    private String description;
    private Task currentTask;
    private int currentTaskId = 0;
    ArrayList<Task> taskIterable;
    private static final String TAG = LandingTask.class.getSimpleName();

    Mission(String title, String description) {
        this(title, description, null);
    }

    Mission(String title, String description, ArrayList<Task> taskIterable) {
        super(title);
        this.description = description;
        this.taskIterable = taskIterable;
    }

    private void nextTask() {
        if (!(currentTaskId < taskIterable.size())) {
            currentState = TaskState.COMPLETED;
            listener.statusUpdate(currentState, "Mission finished.");
            return;
        }

        if (currentTaskId != 0)
            currentTask.setListener(null);
        currentTask = taskIterable.get(currentTaskId);
        currentTaskId += 1;

        currentTask.setListener(this);
        currentTask.start();
    }

    void start() {
        if (currentState != TaskState.READY)
            return;

        currentState = TaskState.RUNNING;
        listener.statusUpdate(currentState, String.format("Mission \"%s\" started", title));
        new Thread(this::nextTask).start();
    }

    void stop() {
        currentTask.stop();
        currentState = TaskState.COMPLETED;
        listener.statusUpdate(currentState, String.format("Mission \"%s\" stopped", title));
    }

    @Override
    public void statusUpdate(Task.TaskState state, String message) {
        // Tasks shouldn't report ready/notready/running
        Log.v(TAG,"Status update: " + state.toString() + ": " + message);
        switch (state) {
            case COMPLETED:
//                listener.statusUpdate(TaskState.RUNNING, message + " " + Integer.toString(currentTaskId));
//                showToast("Task finished: " + message);
                listener.statusUpdate(TaskState.RUNNING, message);
                new Thread(this::nextTask).start();
//                nextTask();
                break;
            case FAILED:
//                showToast("Task failed: " + message);
                listener.statusUpdate(state, message);
                break;
            default:
                break;
        }
    }

    public String getCurrentTaskName() {
        return currentTask.getClass().getSimpleName();
    }

    public String getDescription() {
        return description;
    }

    // missions implement this to display args in saved mission list
    abstract public String argsToString();

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("tasks", taskIterable);
        dest.writeBundle(bundle);
    }
}
