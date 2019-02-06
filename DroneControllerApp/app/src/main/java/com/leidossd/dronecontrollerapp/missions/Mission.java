package com.leidossd.dronecontrollerapp.missions;

import android.os.Parcel;
import android.os.Parcelable;

abstract public class Mission implements Parcelable {
    protected String title;

    Mission(String title) {
        this.title = title;
    }

    Mission(Parcel in) {
        title = in.readString();
    }

    //abstract Mission(Parcel in);

    public String getTitle() {
        return title;
    }

    abstract public void start();
    abstract public void stop();
}
