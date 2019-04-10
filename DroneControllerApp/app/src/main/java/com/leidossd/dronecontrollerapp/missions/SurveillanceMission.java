package com.leidossd.dronecontrollerapp.missions;

import android.os.Parcel;

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

    private SurveillanceMission(String title, ArrayList<Task> tasks){
        super(title, tasks);
        currentState = Task.TaskState.READY;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        write(dest);
    }

    public static SurveillanceMission create(Parcel in){
        String title = in.readString();
        ArrayList<Task> tasks = new ArrayList<>();
        in.readTypedList(tasks, TaskCreator.CREATOR);
        return new SurveillanceMission(title, tasks);
    }

    @Override
    public void write(Parcel out) {
        out.writeString("SURVEILLANCE_MISSION");
        out.writeString(title);
        out.writeTypedList(taskIterable);
    }
}