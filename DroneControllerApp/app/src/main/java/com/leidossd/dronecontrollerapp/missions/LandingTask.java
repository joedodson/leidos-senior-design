package com.leidossd.dronecontrollerapp.missions;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.leidossd.djiwrapper.FlightControllerWrapper;

public class LandingTask extends Task {
    private static final String TAG = LandingTask.class.getSimpleName();

    LandingTask() {
        super("Landing...");
    }

    @Override
    void start() {
        FlightControllerWrapper.getInstance()
                .startLanding((error) -> {
                    if (error != null) {
                        listeners.statusUpdate(TaskState.FAILED, "ERROR IN Landing!");
                        Log.e(TAG, "Could not land: " + error.getDescription());
                    } else {
                        currentState = TaskState.COMPLETED;
                        listeners.statusUpdate(currentState, title + " completed");
                        Log.d(TAG, "Land completed succesfully");
                    }
                });
    }

    @Override
    void stop() {
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public LandingTask createFromParcel(Parcel in) {
            return new LandingTask();

        }

        public LandingTask[] newArray(int size) {
            return new LandingTask[size];
        }
    };
}
