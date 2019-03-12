package com.leidossd.dronecontrollerapp.missions;

import android.os.Parcel;

import com.leidossd.djiwrapper.Coordinate;

import java.util.ArrayList;

public class WaypointMission extends Mission {
    public WaypointMission(Coordinate destination){
        super("Waypoint Mission to " + destination);
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new TakeOffTask());
//        tasks.add(new RotationTask(180));
        tasks.add(new WaitTask(2000));
        tasks.add(new LandingTask());
        taskIterable = tasks;
    }

    WaypointMission(String title, ArrayList<Task> tasks){
        super(title, tasks);
        currentState = Task.TaskState.READY;
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


    public static WaypointMission create(Parcel in){
        String title = in.readString();
        ArrayList<Task> tasks = new ArrayList<>();
        in.readTypedList(tasks, TaskCreator.CREATOR);
        return new WaypointMission(title, tasks);
    }

    @Override
    public void write(Parcel out){
        out.writeString("WAYPOINT_MISSION");
        out.writeString(title);
        out.writeTypedList(taskIterable);
    }
}
