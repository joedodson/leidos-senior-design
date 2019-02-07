package com.leidossd.dronecontrollerapp.missions;

import android.os.Parcel;
import android.os.Parcelable;

import com.leidossd.dronecontrollerapp.MainApplication;

public class SpecificMission extends Mission {

    public SpecificMission(String title){
        super(title);
    }

    public String getTitle() {
        return super.getTitle();
    }

    @Override
    public void start() {
        MainApplication.showToast("Starting");
        status = "RUNNING";
    }

    @Override
    public void stop() {
        MainApplication.showToast("Stopping");
        status = "COMPLETED SUCCESSFULLY";
    }

    public void testing() {
        MainApplication.showToast("Testing");
    }

    @Override
    public int describeContents(){
        return 0;
    }

    // Parcelable functionality
    public SpecificMission(Parcel in) {
        super(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(status);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public SpecificMission createFromParcel(Parcel in) {
            return new SpecificMission(in);
        }

        public SpecificMission[] newArray(int size) {
            return new SpecificMission[size];
        }
    };
}
