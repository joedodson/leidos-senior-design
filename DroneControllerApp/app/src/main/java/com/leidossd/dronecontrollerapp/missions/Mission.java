package com.leidossd.dronecontrollerapp.missions;

import android.os.Parcel;
import android.os.Parcelable;

abstract public class Mission implements Parcelable {
    protected String title;
    protected String status;

    Mission(String title) {
        this.title = title;
    }

    Mission(Parcel in) {
        title = in.readString();
        status = in.readString();
    }

    //abstract Mission(Parcel in);

    public String getTitle() {
        return title;
    }
    public String getStatus() { return status; }

    abstract public void start();
    abstract public void stop();
}
