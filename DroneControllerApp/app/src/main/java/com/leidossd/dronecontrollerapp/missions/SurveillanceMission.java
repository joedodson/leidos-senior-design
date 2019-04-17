package com.leidossd.dronecontrollerapp.missions;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.leidossd.djiwrapper.Coordinate;

import java.util.ArrayList;
import java.util.Locale;

public class SurveillanceMission extends Mission {
    private static String title = "Surveillance Mission";
    private static String description = "Fly to specified waypoint, do surveillance, and then return home";

    private Coordinate destination;

    public SurveillanceMission(Coordinate destination){
        this(title, description, destination);
    }

    SurveillanceMission(String title, String description, Coordinate destination){
        super(title, description);
        this.destination = destination;

        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new TakeOffTask());
        tasks.add(new WaitTask(10000));
        tasks.add(new FlightTask(destination));
        tasks.add(new WaitTask(3000));

        tasks.add(new FlightTask(new Coordinate(0,0,0)));
        tasks.add(new LandingTask());
        taskIterable = tasks;
    }

    private SurveillanceMission(String title, ArrayList<Task> tasks) {
        super(title, description, tasks);
        currentState = Task.TaskState.READY;
    }

    public String argsToString() {
        return String.format(Locale.getDefault(), "Flies to coordinate: %s", this.destination.toString());
    }

    // Parcelable functionality
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public SurveillanceMission createFromParcel(Parcel in) {
            String title = in.readString();

            Bundle taskBundle = in.readBundle(SurveillanceMission.class.getClassLoader());
            ArrayList<Task> tasks = taskBundle.getParcelableArrayList("tasks");
            return new SurveillanceMission(title, tasks);
        }

        public SurveillanceMission[] newArray(int size) {
            return new SurveillanceMission[size];
        }
    };
}