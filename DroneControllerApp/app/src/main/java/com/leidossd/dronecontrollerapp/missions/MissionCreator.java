package com.leidossd.dronecontrollerapp.missions;

import android.os.Parcel;
import android.os.Parcelable;

public class MissionCreator {
    public enum MissionType {
        SPECIFIC_MISSION,
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        @Override
        public Mission createFromParcel(Parcel source) {
            String type = source.readString();

            switch(MissionType.valueOf(type)){
                case SPECIFIC_MISSION:
                    return SpecificMission.create(source);
                default:
                    throw new AssertionError();
            }
//            return null;
        }

        @Override
        public Mission[] newArray(int size) {
            return new Mission[size];
        }
    };
}