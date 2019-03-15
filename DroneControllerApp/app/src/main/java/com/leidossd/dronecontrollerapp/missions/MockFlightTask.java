package com.leidossd.dronecontrollerapp.missions;

import android.os.Parcel;

import java.util.Timer;
import java.util.TimerTask;

public class MockFlightTask extends Task {

    MockFlightTask(){
        super("Mocking a flight");
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString("MOCK_FLIGHT_TASK");
        dest.writeString(title);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    void start(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask(){
            @Override
            public void run(){
                listener.statusUpdate(TaskState.COMPLETED, "Mock Flight Completed");
            }
        }, 2000);
    }

    @Override
    void write(Parcel out){
        out.writeString("MOCK_FLIGHT_TASK");
        out.writeString(title);
    }

    public static MockFlightTask create(Parcel in){
        in.readString();
        return new MockFlightTask();
    }

    @Override
    void stop(){
    }
}
