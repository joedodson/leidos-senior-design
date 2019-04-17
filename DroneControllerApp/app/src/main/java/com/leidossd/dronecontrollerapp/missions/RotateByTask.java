package com.leidossd.dronecontrollerapp.missions;

import android.os.Parcel;
import android.os.Parcelable;

import com.leidossd.djiwrapper.FlightControllerWrapper;

public class RotateByTask extends Task {
    private static final String TAG = RotateByTask.class.getSimpleName();

    private float angle;

    RotateByTask(float angle) {
        super("Rotate by %f" + Float.toString(angle));
        this.angle = angle;
    }

    void start() {        
        FlightControllerWrapper.getInstance().rotateBy(angle, (error) -> {
            if (error != null) {
                listener.statusUpdate(TaskState.FAILED, "ROTATION FAILED! " + error.getDescription());
            }
            else
                listener.statusUpdate(TaskState.COMPLETED, "ROTATION SUCCESS");
        });
    }

    void stop() {

    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public RotateByTask createFromParcel(Parcel in) {
            return new RotateByTask(in.readFloat());

        }

        public RotateByTask[] newArray(int size) {
            return new RotateByTask[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(angle);
    }

}
