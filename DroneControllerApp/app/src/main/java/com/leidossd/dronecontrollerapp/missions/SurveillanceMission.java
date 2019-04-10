package com.leidossd.dronecontrollerapp.missions;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.leidossd.djiwrapper.Coordinate;

import java.util.ArrayList;

public class SurveillanceMission extends Mission {
    public SurveillanceMission(String title, Coordinate coord, float cameraAngle) {
        super(title);
        ArrayList<Task> tasks = new ArrayList<>();
        //TODO: Fill with necessary tasks.
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