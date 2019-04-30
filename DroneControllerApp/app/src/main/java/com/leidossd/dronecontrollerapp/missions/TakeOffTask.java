package com.leidossd.dronecontrollerapp.missions;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.leidossd.djiwrapper.FlightControllerWrapper;


public class TakeOffTask extends Task {
    private static final String TAG = TakeOffTask.class.getSimpleName();

    TakeOffTask() {
        super("Taking Off...");
    }

    @Override
    void start() {
        FlightControllerWrapper.getInstance()
                .startTakeoff((error) -> {
                    if (error != null) {
                        if (FlightControllerWrapper.getInstance().compassHasError()) {
                            listeners.statusUpdate(TaskState.FAILED, "Compass needs to be calibrated!");
                            Log.e(TAG, "Could not takeoff: compass not calibrated");
                        } else {
                            listeners.statusUpdate(TaskState.FAILED, error.toString());
                            Log.e(TAG, "Could not take off: " + error.getDescription());
                        }
                    } else {
                        currentState = TaskState.COMPLETED;
                        listeners.statusUpdate(currentState, title + " completed");
                    }
                });
//            if (FlightControllerWrapper.getInstance().isAirborne())
//                listeners.statusUpdate(TaskState.COMPLETED, title + " completed");
    }

    @Override
    void stop() {
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public TakeOffTask createFromParcel(Parcel in) {
            return new TakeOffTask();

        }

        public TakeOffTask[] newArray(int size) {
            return new TakeOffTask[size];
        }
    };
}

