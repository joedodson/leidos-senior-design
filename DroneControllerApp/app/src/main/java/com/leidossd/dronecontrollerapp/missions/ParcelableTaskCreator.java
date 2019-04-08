package com.leidossd.dronecontrollerapp.missions;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableTaskCreator {
    public enum TaskType {
        WAIT_TASK,
        TOAST_TASK,
        TAKEOFF_TASK,
        LANDING_TASK,
        FLIGHT_TASK,
        ROTATION_TASK
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        @Override
        public Task createFromParcel(Parcel source) {
            String type = source.readString();

            if(type.matches(".*?MISSION$"))
                return (Mission) ParcelableMissionCreator.CREATOR.createFromParcel(source);

            switch(TaskType.valueOf(type)){
                case WAIT_TASK:
                    return WaitTask.create(source);
                case TOAST_TASK:
                    return ToastTask.create(source);
                case TAKEOFF_TASK:
                    return TakeOffTask.create(source);
                case LANDING_TASK:
                    return LandingTask.create(source);
//                case FLIGHT_TASK:
//                    return FlightTask.CREATOR.createFromParcel(source);
//                case ROTATION_TASK:
//                    return RotationTask.CREATOR.createFromParcel(source);
                default:
                    throw new AssertionError();
            }
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };
}
