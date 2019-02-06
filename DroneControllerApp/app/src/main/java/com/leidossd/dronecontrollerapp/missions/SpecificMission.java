package com.leidossd.dronecontrollerapp.missions;

import android.os.Parcel;
import android.os.Parcelable;

public class SpecificMission extends Mission {

    public SpecificMission(String title){
        super(title);
    }

    public String getTitle() {
        return super.getTitle();
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    public void testing() {

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
        dest.writeStringArray(new String[] {
                this.title
        });
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
