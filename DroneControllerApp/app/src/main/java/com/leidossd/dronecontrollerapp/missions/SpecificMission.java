package com.leidossd.dronecontrollerapp.missions;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class SpecificMission extends Mission {
    public SpecificMission(String title){

        super(title);
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new ToastTask("Starting..."));
        tasks.add(new WaitTask(5000));
        tasks.add(new SetGimbalAngleTask(-45));
        tasks.add(new ToastTask("1..."));
        tasks.add(new WaitTask(5000));
        tasks.add(new SetGimbalAngleTask(-90));
        tasks.add(new ToastTask("2..."));
        tasks.add(new WaitTask(5000));
        tasks.add(new SetGimbalAngleTask(0));
        tasks.add(new ToastTask("3..."));
        tasks.add(new WaitTask(5000));
        tasks.add(new ToastTask("Ending..."));
        taskIterable = tasks;
    }

    SpecificMission(String title, ArrayList<Task> tasks){
        super(title, tasks);
        currentState = TaskState.READY;
    }

    // Parcelable functionality
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public SpecificMission createFromParcel(Parcel in) {
            String title = in.readString();
            Bundle taskBundle = in.readBundle(SpecificMission.class.getClassLoader());
            ArrayList<Task>tasks = taskBundle.getParcelableArrayList("tasks");
            return new SpecificMission(title, tasks);
        }

        public SpecificMission[] newArray(int size) {
            return new SpecificMission[size];
        }
    };
}
