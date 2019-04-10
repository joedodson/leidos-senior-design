package com.leidossd.dronecontrollerapp.missions;

import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;

import com.leidossd.djiwrapper.CoordinateFlightControl;
import com.leidossd.djiwrapper.FlightControllerWrapper;

import static com.leidossd.dronecontrollerapp.MainApplication.showToast;

public class RotationTask extends Task {
    private static final String TAG = RotationTask.class.getSimpleName();

    private float angle;

    RotationTask(float angle) {
        super("Rotate to %f" + Float.toString(angle));
        this.angle = angle;
    }

    void start() {
        FlightControllerWrapper.getInstance()
                .setFlightMode(CoordinateFlightControl.FlightMode.ABSOLUTE);

        FlightControllerWrapper.getInstance().rotateTo(angle, (error) -> {
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
        public RotationTask createFromParcel(Parcel in) {
            return new RotationTask(in.readFloat());

        }

        public RotationTask[] newArray(int size) {
            return new RotationTask[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(angle);
    }

}
