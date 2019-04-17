package com.leidossd.dronecontrollerapp.missions;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.leidossd.djiwrapper.Coordinate;

import java.util.ArrayList;
import java.util.Locale;

public class WaypointMission extends Mission {
    private static String title = "Waypoint Mission";
    private static String description = "Fly to specified waypoint and then return home";
    private Coordinate destination;

    public WaypointMission(Coordinate destination) {
        super(title, description);

        this.destination = destination;
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

    private WaypointMission(String title, ArrayList<Task> tasks) {
        super(title, description, tasks);
        currentState = Task.TaskState.READY;
    }

    public String argsToString() {
        return String.format(Locale.getDefault(), "Flies to coordinate: %s", this.destination.toString());
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
