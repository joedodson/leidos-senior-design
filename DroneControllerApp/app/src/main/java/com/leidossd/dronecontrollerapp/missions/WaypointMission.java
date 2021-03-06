package com.leidossd.dronecontrollerapp.missions;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.leidossd.djiwrapper.Coordinate;
import com.leidossd.djiwrapper.FlightControllerWrapper;

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
        tasks.add(new WaitTask(7000));
//        tasks.add(new RotateToTask(180));
        FlightControllerWrapper.getInstance().setRotationLock(false);
//        tasks.add(new RotateToTask(180));
        tasks.add(new FlightTask(new Coordinate(1, 1, 1)));
//        tasks.add(new WaitTask(2000));
//        tasks.add(new FlightTask(new Coordinate(0, 1, 0)));
//        tasks.add(new RotateToTask(90));
//        tasks.add(new FlightTask(new Coordinate(0, -1, 0)));
//        tasks.add(new WaitTask(2000));
//        tasks.add(new FlightTask(new Coordinate(0, 0, 0)));
//        tasks.add(new RotateByTask(-180));
//        tasks.add(new WaitTask(2000));
//        tasks.add(new WaitTask(2000));
//        tasks.add(new RotateToTask(0));
//        tasks.add(new WaitTask(1000));
//        tasks.add(new SetGimbalAngleTask(-20));
//        tasks.add(new WaitTask(2000));
//        tasks.add(new StartRecordingTask());
//        tasks.add(new WaitTask(1000));
//        tasks.add(new RotateByTask(360));
//        tasks.add(new WaitTask(2000));
//        tasks.add(new WaitTask(1000));
//        tasks.add(new StopRecordingTask());
        tasks.add(new WaitTask(2000));
        tasks.add(new FlightTask(new Coordinate(-1,0,0)));
        tasks.add(new WaitTask(2000));
        tasks.add(new FlightTask(new Coordinate(1,-1,(float)1.5)));
        tasks.add(new WaitTask(2000));
        tasks.add(new SetGimbalAngleTask(-20));
        tasks.add(new WaitTask(2000));
        tasks.add(new StartRecordingTask());
        tasks.add(new WaitTask(1000));
        tasks.add(new RotateByTask(360));
        tasks.add(new WaitTask(1000));
        tasks.add(new StopRecordingTask());
        tasks.add(new WaitTask(2000));
        tasks.add(new FlightTask(new Coordinate(0,0,0)));
        tasks.add(new WaitTask(2000));
        tasks.add(new RotateToTask(0));
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
