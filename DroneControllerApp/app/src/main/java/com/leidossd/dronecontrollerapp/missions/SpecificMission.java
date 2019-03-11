package com.leidossd.dronecontrollerapp.missions;

import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SpecificMission extends Mission {
    public static final Creator<SpecificMission> CREATOR = new Creator<SpecificMission>(){
        @Override
        public SpecificMission createFromParcel(Parcel source) {
            String title = source.readString();
            LinkedList<Task> tasks = null;
            source.readParcelableArray(tasks, CREATOR);
            source.readTypedObject()
            return null;
        }

        @Override
        public SpecificMission[] newArray(int size) {
            return new SpecificMission[size];
        }
    };

    public SpecificMission(String title){
        super(title);
        List<Task> tasks = new LinkedList<>();
        tasks.add(new ToastTask("Starting..."));
        tasks.add(new WaitTask(5000));
        tasks.add(new ToastTask("Ending..."));

        taskIterable = tasks;
    }

    @Override
    public int describeContents(){
        return 0;
    }

    // Parcelable functionality
    private SpecificMission(Parcel in) {

        super(in.readString(), in.readTypedList(k, SpecificMission.CREATOR));
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeTypedList((LinkedList<Task>) taskIterable);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public SpecificMission createFromParcel(Parcel in) {
            return new SpecificMission(in);
        }

        public SpecificMission[] newArray(int size) {
            return new SpecificMission[size];
        }
    };

    @Override
    public void statusUpdate(TaskState status, String message){
        listener.statusUpdate(status, message);

    }
}
