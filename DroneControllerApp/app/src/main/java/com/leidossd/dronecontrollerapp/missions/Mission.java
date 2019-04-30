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
        this(title, description, new ArrayList<>());
    }

    Mission(String title, String description, ArrayList<Task> taskIterable) {
        super(title);
        this.description = description;
        this.taskIterable = taskIterable;
    }

    private void nextTask() {
        if (currentTaskId >= taskIterable.size()) {
            stop();
            return;
        }

        currentTask = taskIterable.get(currentTaskId++);

        currentTask.addListener(this);
        currentTask.start();
    }

    public void start() {
        if (currentState != TaskState.READY)
            return;

        currentState = TaskState.RUNNING;
        listeners.statusUpdate(currentState, String.format("Mission \"%s\" started", title));

        new Thread(this::nextTask).start();
    }

    public void stop() {
        currentTask.stop();
        currentState = TaskState.COMPLETED;
        listeners.statusUpdate(currentState, String.format("Mission \"%s\" finished", title));
    }

    @Override
    public void statusUpdate(Task.TaskState state, String message) {
        // Tasks shouldn't report ready/notready/running
        Log.v(TAG,"Status update: " + state.toString() + ": " + message);
        switch (state) {
            case COMPLETED:
                listeners.statusUpdate(TaskState.RUNNING, message);
                new Thread(this::nextTask).start();
                break;
            case FAILED:
                listeners.statusUpdate(state, message);
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
