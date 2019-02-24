package com.leidossd.dronecontrollerapp.missions;

import android.os.Parcel;
import android.os.Parcelable;

abstract public class Mission implements Parcelable {
    protected String title;
    MissionState currentState;

    MissionUpdateCallback missionUpdateCallback;

    Mission(String title) {
        this(title, null);
    }

    Mission(String title, MissionUpdateCallback missionUpdateCallback) {
        this.title = title;
        this.missionUpdateCallback = missionUpdateCallback;
        this.currentState = MissionState.NOT_READY;
    }

    Mission(Parcel in) {
        title = in.readString();
        currentState = MissionState.valueOf(in.readString());
    }

    public String getTitle() {
        return title;
    }
    public String getStatus() { return currentState.toString(); }

    public interface MissionUpdateCallback {
        void onMissionStart(String missionStartResult);
        void onMissionFinish(String missionFinishResult);
        void onMissionError(String missionErrorMessage);
    }

    void setMissionUpdateCallback(MissionUpdateCallback missionUpdateCallback) {
        this.missionUpdateCallback = missionUpdateCallback;
    }

    MissionUpdateCallback getMissionUpdateCallback() {
        return missionUpdateCallback;
    }

    abstract protected void start();
    abstract protected void stop();

    public enum MissionState {
        NOT_READY,
        READY,
        RUNNING,
        COMPLETED,
        FAILED
    }
}
