package com.leidossd.dronecontrollerapp.missions;

import android.os.Parcel;

import java.util.ArrayList;

public class SpecificMission extends Mission {
    public SpecificMission(String title){

        super(title);
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new ToastTask("Starting..."));
        tasks.add(new WaitTask(5000));
        tasks.add(new ToastTask("1..."));
        tasks.add(new WaitTask(5000));
        tasks.add(new ToastTask("2..."));
        tasks.add(new WaitTask(5000));
        tasks.add(new ToastTask("3..."));
        tasks.add(new WaitTask(5000));
        tasks.add(new ToastTask("Ending..."));
        taskIterable = tasks;
    }

    SpecificMission(String title, ArrayList<Task> tasks){
        super(title, tasks);
        currentState = TaskState.READY;
    }

    @Override
    public int describeContents(){
        return 0;
    }

    // Parcelable functionality

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        write(dest);
    }


    public static SpecificMission create(Parcel in){
        String title = in.readString();
        ArrayList<Task> tasks = new ArrayList<>();
        in.readTypedList(tasks, TaskCreator.CREATOR);
        return new SpecificMission(title, tasks);
    }

    @Override
    public void write(Parcel out){
        out.writeString("SPECIFIC_MISSION");
        out.writeString(title);
        out.writeTypedList(taskIterable);
    }
}
