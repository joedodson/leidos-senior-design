package com.leidossd.dronecontrollerapp.missions;

import java.util.ArrayList;


abstract public class Mission extends Task implements Task.StatusUpdateListener {
    private Task currentTask;
    private int currentTaskId = 0;
    ArrayList<Task> taskIterable;
    public static final Creator CREATOR = ParcelableMissionCreator.CREATOR;

    Mission(String title) {
        super(title);
    }

    Mission(String title, ArrayList<Task> taskIterable){
        super(title);
        this.taskIterable = taskIterable;
    }

    private void nextTask(){
        if(!(currentTaskId < taskIterable.size())) {
            currentState = TaskState.COMPLETED;
            listener.statusUpdate(currentState, "Mission finished.");
            return;
        }

        currentTask = taskIterable.get(currentTaskId);
        currentTaskId += 1;

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
//                listener.statusUpdate(TaskState.RUNNING, message + " " + Integer.toString(currentTaskId));
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
