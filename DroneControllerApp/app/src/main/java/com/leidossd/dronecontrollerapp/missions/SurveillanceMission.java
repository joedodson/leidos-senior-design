package com.leidossd.dronecontrollerapp.missions;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.leidossd.djiwrapper.Coordinate;

import java.util.ArrayList;

public class SurveillanceMission extends Mission {
    public SurveillanceMission(String title, Coordinate destination, float cameraAngle){
        super(title);
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new TakeOffTask());
//        tasks.add(new RotationTask(180));
        tasks.add(new WaitTask(10000));
        tasks.add(new FlightTask(destination));
        tasks.add(new WaitTask(3000));
//        tasks.add(new SetGimbalAngleTask(cameraAngle));
//        tasks.add(new SetGimbalAngleTask(0));
        tasks.add(new WaitTask(5000));
        tasks.add(new FlightTask(new Coordinate(0,0,0)));
        tasks.add(new LandingTask());
        taskIterable = tasks;
    }

    private SurveillanceMission(String title, ArrayList<Task> tasks) {
        super(title, tasks);
        currentState = Task.TaskState.READY;
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