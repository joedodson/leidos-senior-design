package com.leidossd.dronecontrollerapp.missions;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.leidossd.djiwrapper.Coordinate;

import java.util.ArrayList;

public class WaypointMission extends Mission {
    public WaypointMission(Coordinate destination) {
        super("Waypoint Mission to " + destination);
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new TakeOffTask());
        tasks.add(new WaitTask(10000));
        tasks.add(new FlightTask(new Coordinate(0, 1, 0)));
        tasks.add(new WaitTask(2000));
        tasks.add(new FlightTask(new Coordinate(0, -1, 0)));
        tasks.add(new WaitTask(2000));
        tasks.add(new FlightTask(new Coordinate(0, 0, 0)));
        tasks.add(new WaitTask(2000));
        tasks.add(new LandingTask());
        taskIterable = tasks;
    }

    WaypointMission(String title, ArrayList<Task> tasks) {
        super(title, tasks);
        currentState = Task.TaskState.READY;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public WaypointMission createFromParcel(Parcel in) {
            String title = in.readString();
            Bundle taskBundle = in.readBundle(WaypointMission.class.getClassLoader());
            ArrayList<Task> tasks = taskBundle.getParcelableArrayList("tasks");
            return new WaypointMission(title, tasks);
        }

        public WaypointMission[] newArray(int size) {
            return new WaypointMission[size];
        }
    };
}
