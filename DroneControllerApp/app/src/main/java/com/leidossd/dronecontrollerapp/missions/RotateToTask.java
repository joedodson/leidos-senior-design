
package com.leidossd.dronecontrollerapp.missions;

import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;

import com.leidossd.djiwrapper.FlightControllerWrapper;

import static com.leidossd.dronecontrollerapp.MainApplication.showToast;

public class RotateToTask extends Task {
    private static final String TAG = RotateToTask.class.getSimpleName();

    private float angle;

    RotateToTask(float angle) {
        super("Rotate by %f" + Float.toString(angle));
        this.angle = angle;
    }

    void start() {
        FlightControllerWrapper.getInstance().rotateTo(angle, (error) -> {
            if (error != null) {
                listeners.statusUpdate(TaskState.FAILED, "ROTATION FAILED! " + error.getDescription());
            }
            else
                listeners.statusUpdate(TaskState.COMPLETED, "ROTATION SUCCESS");
        });
    }

    void stop() {

    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public RotateToTask createFromParcel(Parcel in) {
            return new RotateToTask(in.readFloat());

        }

        public RotateToTask[] newArray(int size) {
            return new RotateToTask[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(angle);
    }

}
