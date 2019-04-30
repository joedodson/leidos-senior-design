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

//        Test 1
//        taskIterable.add(new ToastTask("Starting..."));
//        taskIterable.add(new WaitTask(5000));
//        taskIterable.add(new SetGimbalAngleTask(-45));
//        taskIterable.add(new ToastTask("1..."));
//        taskIterable.add(new WaitTask(5000));
//        taskIterable.add(new SetGimbalAngleTask(-90));
//        taskIterable.add(new ToastTask("2..."));
//        taskIterable.add(new WaitTask(5000));
//        taskIterable.add(new SetGimbalAngleTask(0));
//        taskIterable.add(new ToastTask("3..."));
//        taskIterable.add(new WaitTask(5000));
//        taskIterable.add(new ToastTask("Ending..."));

//        Test 2
//        taskIterable.add(new TakeOffTask());
//        taskIterable.add(new WaitTask(7000));
//        taskIterable.add(new RotateByTask(90));
//        taskIterable.add(new WaitTask(1000));
//        taskIterable.add(new RotateByTask(-90));
//        taskIterable.add(new WaitTask(1000));
//        taskIterable.add(new LandingTask());

//        Test 3
        taskIterable.add(new ToastTask("Starting"));
        taskIterable.add(new WaitTask(2000));
        taskIterable.add(new ToastTask("Waiting 5 seconds"));
        taskIterable.add(new WaitTask(5000));
        taskIterable.add(new ToastTask("Finished"));


//        Test 4
//        taskIterable.add(new TakeOffTask());
//        taskIterable.add(new WaitTask(7000));
//        taskIterable.add(new RotateToTask(180));
//        FlightControllerWrapper.getInstance().setRotationLock(false);
//        taskIterable.add(new RotateToTask(180));
//        taskIterable.add(new FlightTask(new Coordinate(1, 1, 1)));
//        taskIterable.add(new WaitTask(2000));
//        taskIterable.add(new FlightTask(new Coordinate(0, 1, 0)));
//        taskIterable.add(new RotateToTask(90));
//        taskIterable.add(new FlightTask(new Coordinate(0, -1, 0)));
//        taskIterable.add(new WaitTask(2000));
//        taskIterable.add(new FlightTask(new Coordinate(0, 0, 0)));
//        taskIterable.add(new RotateByTask(-180));
//        taskIterable.add(new WaitTask(2000));
//        taskIterable.add(new WaitTask(2000));
//        taskIterable.add(new RotateToTask(0));
//        taskIterable.add(new WaitTask(1000));
//        taskIterable.add(new SetGimbalAngleTask(-20));
//        taskIterable.add(new WaitTask(2000));
//        taskIterable.add(new StartRecordingTask());
//        taskIterable.add(new WaitTask(1000));
//        taskIterable.add(new RotateByTask(360));
//        taskIterable.add(new WaitTask(2000));
//        taskIterable.add(new WaitTask(1000));
//        taskIterable.add(new StopRecordingTask());


//        Test 5
//        taskIterable.add(new TakeOffTask());
//        taskIterable.add(new WaitTask(6000));
//        taskIterable.add(new FlightTask(new Coordinate(0,5,0)));
//        taskIterable.add(new WaitTask(500));
//        taskIterable.add(new FlightTask(new Coordinate(0,0,0)));
//        taskIterable.add(new WaitTask(500));
//        taskIterable.add(new RotateToTask(0));
//        taskIterable.add(new WaitTask(500));
//        taskIterable.add(new LandingTask());
//        taskIterable = taskIterable;
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
