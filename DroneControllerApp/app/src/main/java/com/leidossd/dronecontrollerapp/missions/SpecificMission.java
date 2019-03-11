package com.leidossd.dronecontrollerapp.missions;

import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.GridLayout;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SpecificMission extends Mission {
//        @Override
//        public SpecificMission createFromParcel(Parcel source) {
////            Bundle bundle = source.readBundle((ClassLoader) CREATOR);
////            String title = bundle.getString("title");
////            ArrayList<Task> tasks = bundle.getParcelableArrayList("tasks");
////            return new SpecificMission(title, tasks);
//            String title = source.readString();
//            ArrayList<Task> tasks = new ArrayList<>();
//            source.readTypedList(tasks, TaskCreator.CREATOR);
//            return new SpecificMission(title, tasks);
//        }
//
//        @Override
//        public SpecificMission[] newArray(int size) {
//            return new SpecificMission[size];
//        }
//    };
//
    public SpecificMission(String title){
        super(title);
        ArrayList<Task> tasks = new ArrayList<>();
//        tasks.add(new ToastTask("Starting..."));
        tasks.add(new WaitTask(5000));
        tasks.add(new WaitTask(5000));
        taskIterable = tasks;
    }

    SpecificMission(String title, Iterable<Task> tasks){
        super(title, tasks);
        currentState = TaskState.READY;
    }

//    private SpecificMission(Parcel in){
//        super(in.readBundle().getString("title"), in.readBundle().getParcelableArrayList("tasks"));
//    }

    @Override
    public int describeContents(){
        return 0;
    }

    // Parcelable functionality

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        write(dest);
//        dest.writeString("SPECIFIC_MISSION");
//        dest.writeString(title);
//        dest.writeTypedList((ArrayList<Task>) taskIterable);
//        Bundle bndl = new Bundle();
//        bndl.putString("title", title);
//        bndl.putParcelableArrayList("tasks", (ArrayList<Task>) taskIterable);
//        dest.writeBundle(bndl);
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
        out.writeTypedList((ArrayList<Task>) taskIterable);
    }

    @Override
    public void statusUpdate(TaskState status, String message){
        listener.statusUpdate(status, message);

    }
}
