package com.leidossd.dronecontrollerapp;

import com.leidossd.djiwrapper.Mission;

//Interface used in fragments for creating missions.  Allows createMissionActivity to only
//have to implement one function for all fragments.
public interface MissionCreateListener {
    void createMission(Mission mission);
}
