package com.leidossd.dronecontrollerapp.missions;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Timer;
import java.util.TimerTask;

public class MockFlightTask extends Task {

    MockFlightTask() {
        super("Mocking a flight");
    }

    @Override
    void start() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                listeners.statusUpdate(TaskState.COMPLETED, "Mock Flight Completed");
            }
        }, 2000);
    }

    public static MockFlightTask create(Parcel in) {
        in.readString();
        return new MockFlightTask();
    }

    @Override
    void stop() {
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public MockFlightTask createFromParcel(Parcel in) {
            return new MockFlightTask();
        }

        public MockFlightTask[] newArray(int size) {
            return new MockFlightTask[size];
        }
    };
}
