package com.leidossd.dronecontrollerapp.missions;

import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;

import com.leidossd.dronecontrollerapp.MainApplication;

public class SpecificMission extends Mission{

    public SpecificMission(String title){
        super(title, new MissionUpdateCallback() {
            @Override
            public void onMissionStart(String missionStartResult) { }

            @Override
            public void onMissionFinish(String missionFinishResult) { }

            @Override
            public void onMissionError(String missionErrorMessage) { }
        });
    }

    public SpecificMission(String title, MissionUpdateCallback missionUpdateCallback) {
        super(title, missionUpdateCallback);
        this.missionUpdateCallback = missionUpdateCallback;
    }

    public String getTitle() {
        return title;
    }

    @Override
    protected void start() {
        currentState = MissionState.RUNNING;
        Handler handler = new Handler();
        handler.postDelayed(() -> stop(), 5000);
        missionUpdateCallback.onMissionStart(currentState.toString());
    }

    @Override
    protected void stop() {
        currentState = MissionState.COMPLETED;
        missionUpdateCallback.onMissionFinish("from mission " + currentState.toString());
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
        dest.writeString(currentState.toString());
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
