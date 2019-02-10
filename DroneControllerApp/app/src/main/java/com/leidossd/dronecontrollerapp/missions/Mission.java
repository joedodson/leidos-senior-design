package com.leidossd.dronecontrollerapp.missions;

import android.os.Parcel;
import android.os.Parcelable;

abstract public class Mission implements Parcelable {
    protected String title;
    protected String status;

    protected MissionUpdateCallback missionUpdateCallback;

    Mission(String title) {
        this(title, null);
    }

    Mission(String title, MissionUpdateCallback missionUpdateCallback) {
        this.title = title;
        this.missionUpdateCallback = missionUpdateCallback;
    }

    Mission(Parcel in) {
        title = in.readString();
        status = in.readString();
    }

    public String getTitle() {
        return title;
    }
    public String getStatus() { return status; }

    public interface MissionUpdateCallback {
        void onMissionStart(String missionStartResult);
        void onMissionFinish(String missionFinishResult);
    }

    public void setMissionUpdateCallback(MissionUpdateCallback missionUpdateCallback) {
        this.missionUpdateCallback = missionUpdateCallback;
    }

    abstract protected void start();
    abstract protected void stop();
}
