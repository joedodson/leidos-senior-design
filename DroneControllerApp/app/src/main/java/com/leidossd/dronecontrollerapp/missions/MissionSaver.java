package com.leidossd.dronecontrollerapp.missions;

import android.app.Activity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Locale;

//TODO: Replace "Drone flies." with actual description; have Mission print out type in the form of a String.

/**
 * Mission Adapter Class:
 * Handles saving and loading of missions, as well as managing a list of saved missions. Sends and retrieves
 * mission data in the form of a MissionFrame.  Uses json files to record mission data.
 */

class MissionSaver {
    //Number of default missions to add (if default missions are enabled).
    private static final int NUM_DEFAULT = 3;
    private int DEFAULT_ID = 1;
    private Activity attachedActivity;
    private boolean addDefault;
    private ArrayList<MissionFrame> savedMissions;

    MissionSaver(Activity activity){
        this(activity, false);
    }

    MissionSaver(Activity activity, boolean addDefault){
        savedMissions = new ArrayList<>();
        attachedActivity = activity;
        this.addDefault = addDefault;
    }

    int size(){
        return savedMissions.size();
    }

    MissionFrame getMissionFrameAt(int pos){
        return savedMissions.get(pos);
    }

    void unloadMissions(){
        File directory = attachedActivity.getExternalFilesDir(null);
        if(directory != null) {
            File[] files = directory.listFiles();
            if(addDefault){
                for(int i = 0; i < NUM_DEFAULT; i++) {
                    String des = "Drone flies.";
                    savedMissions.add(new MissionFrame(String.format(Locale.getDefault(), "Default Mission %d", DEFAULT_ID++), "Default Mission", false, des));
                }
            }
            for (File f : files) {
                try {
                    JSONObject json = loadFile(f);
                    String missionTitle = json.getString("Title");
                    String des = "Drone flies.";
                    savedMissions.add(new MissionFrame(missionTitle, "Waypoint Mission", false, des));
                } catch (JSONException je) {
                    je.printStackTrace();
                }
            }
        } else {
            throw new RuntimeException("Could not load missions from default directory");
        }
    }

    void saveMission(MissionFrame missionFrame){
        JSONObject jso = missionToJSON(missionFrame);
        try{
            Writer output = null;
            File file = new File(attachedActivity.getExternalFilesDir(null), String.format("%s.json",missionFrame.getMissionName()));
            output = new BufferedWriter(new FileWriter(file));
            output.write(jso.toString());
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        savedMissions.add(missionFrame);
    }

    private JSONObject loadFile(File file){
        JSONObject missionFile = null;
        try {
            InputStream is = new FileInputStream(file.getPath());
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonStr = new String(buffer);
            missionFile = new JSONObject(jsonStr);
        } catch (IOException ie) {
            ie.printStackTrace();
        } catch (JSONException je) {
            je.printStackTrace();
        }
        return missionFile;
    }

    private JSONObject missionToJSON(MissionFrame mission){
        JSONObject jso = new JSONObject();
        try {
            jso.put("Title", mission.getMissionName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jso;
    }
}
