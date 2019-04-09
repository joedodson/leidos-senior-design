package com.leidossd.dronecontrollerapp.missions;

/**
 * Mission Frame:
 * Save file representation of a mission.  Makes it easy to visualize mission data
 * in the form of file input, and separates saving missions from using them.
 */

class MissionFrame {
    private final String missionName;
    private final String missionType;
    private final boolean isDefault;
    private final String missionDescription;

    MissionFrame(String missionName, String missionType, boolean isDefault, String missionDescription) {
        this.missionName = missionName;
        this.missionType = missionType;
        this.isDefault = isDefault;
        this.missionDescription = missionDescription;
    }

    String getMissionName() {
        return missionName;
    }

    String getMissionType() {
        return missionType;
    }

    boolean getIsDefault() {
        return isDefault;
    }

    String getMissionDescription() {
        return missionDescription;
    }
}
