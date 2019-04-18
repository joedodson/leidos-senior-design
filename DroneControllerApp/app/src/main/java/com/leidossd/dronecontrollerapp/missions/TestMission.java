package com.leidossd.dronecontrollerapp.missions;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.leidossd.djiwrapper.Coordinate;
import com.leidossd.djiwrapper.FlightControllerWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TestMission extends Mission {
    private static String title = "Test Mission";
    private static String description = "Testing mission for developers - who knows what it'll do...";

    public TestMission() {
        this(title);
    }
    public TestMission(String title) {
        super(title, description);
        ArrayList<Task> tasks = new ArrayList<>();

//        Test 1
//        tasks.add(new ToastTask("Starting..."));
//        tasks.add(new WaitTask(5000));
//        tasks.add(new SetGimbalAngleTask(-45));
//        tasks.add(new ToastTask("1..."));
//        tasks.add(new WaitTask(5000));
//        tasks.add(new SetGimbalAngleTask(-90));
//        tasks.add(new ToastTask("2..."));
//        tasks.add(new WaitTask(5000));
//        tasks.add(new SetGimbalAngleTask(0));
//        tasks.add(new ToastTask("3..."));
//        tasks.add(new WaitTask(5000));
//        tasks.add(new ToastTask("Ending..."));

//      Test 2
//        tasks.add(new TakeOffTask());
//        tasks.add(new WaitTask(7000));
//        tasks.add(new RotateByTask(90));
//        tasks.add(new WaitTask(1000));
//        tasks.add(new RotateByTask(-90));
//        tasks.add(new WaitTask(1000));
//        tasks.add(new LandingTask());

//        tasks.add(new ToastTask("Starting"));
//        tasks.add(new WaitTask(2000));
//        tasks.add(new ToastTask("Moving Gimbal in 2 seconds"));
//        tasks.add(new SetGimbalAngleTask(-90));
//        tasks.add(new WaitTask(2000));
//        tasks.add(new SetGimbalAngleTask(0));
//        tasks.add(new ToastTask("Done"));



//                tasks.add(new TakeOffTask());
//        tasks.add(new WaitTask(7000));
//        tasks.add(new RotateToTask(180));
//        FlightControllerWrapper.getInstance().setRotationLock(false);
//        tasks.add(new RotateToTask(180));
//        tasks.add(new FlightTask(new Coordinate(1, 1, 1)));
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


        // test 3

        tasks.add(new TakeOffTask());
        tasks.add(new WaitTask(6000));
        tasks.add(new FlightTask(new Coordinate(0,5,0)));
        tasks.add(new WaitTask(500));
        tasks.add(new FlightTask(new Coordinate(0,0,0)));
        tasks.add(new WaitTask(500));
        tasks.add(new RotateToTask(0));
        tasks.add(new WaitTask(500));
        tasks.add(new LandingTask());
        taskIterable = tasks;
    }

    TestMission(String title, ArrayList<Task> tasks) {
        super(title, description, tasks);
        currentState = TaskState.READY;
    }

    public List<String> getTaskNames() {
        return taskIterable.stream().map(task -> task.getClass().getSimpleName()).collect(Collectors.toList());
    }

    public String argsToString() {
        return String.format("Tasks: %s", getTaskNames());
    }

    // Parcelable functionality
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public TestMission createFromParcel(Parcel in) {
            String title = in.readString();
            Bundle taskBundle = in.readBundle(TestMission.class.getClassLoader());
            ArrayList<Task> tasks = taskBundle.getParcelableArrayList("tasks");
            return new TestMission(title, tasks);
        }

        public TestMission[] newArray(int size) {
            return new TestMission[size];
        }
    };
}
