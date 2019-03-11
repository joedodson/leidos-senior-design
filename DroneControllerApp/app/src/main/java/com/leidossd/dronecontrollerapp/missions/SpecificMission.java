package com.leidossd.dronecontrollerapp.missions;

import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SpecificMission extends Mission {
    public static final Creator<SpecificMission> CREATOR = new Creator<SpecificMission>(){
        @Override
        public SpecificMission createFromParcel(Parcel source) {
//            Bundle bundle = source.readBundle();
//            String title = bundle.getParcelable("title");
//            ArrayList<Task> tasks = bundle.getParcelableArrayList("tasks");
            return new SpecificMission(source);
        }

        @Override
        public SpecificMission[] newArray(int size) {
            return new SpecificMission[size];
        }
    };

    public SpecificMission(String title){
        super(title);
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new ToastTask("Starting..."));
        tasks.add(new WaitTask(5000));
        tasks.add(new ToastTask("Ending..."));

        taskIterable = tasks;
    }

    private SpecificMission(Parcel in){
        super(in.readString(), in.readBundle().getParcelableArrayList("tasks"));
    }

    @Override
    public int describeContents(){
        return 0;
    }

    // Parcelable functionality

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        Bundle bndl = new Bundle();
        bndl.putParcelableArrayList("tasks", (ArrayList<Task>) taskIterable);
        dest.writeBundle(bndl);
    }

    @Override
    public void statusUpdate(TaskState status, String message){
        listener.statusUpdate(status, message);

    }
}
