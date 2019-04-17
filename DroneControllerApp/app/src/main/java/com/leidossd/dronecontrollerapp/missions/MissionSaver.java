package com.leidossd.dronecontrollerapp.missions;

import android.app.Activity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Mission Adapter Class:
 * Handles saving and loading of missions, as well as managing a list of saved missions. Sends and retrieves
 * mission data to the MissionSaveAdapter.  Uses json files to record mission data.
 *
 * The Gson library is used to handle conversion of all class variables and data to Json form.
 */

class MissionSaver {
    //Number of default missions to add (if default missions are enabled).
    private static final int NUM_DEFAULT = 3;
    private int DEFAULT_ID = 1;
    private Activity attachedActivity;
    private boolean addDefault;
    private ArrayList<Mission> savedMissions;
    private GsonBuilder gBuilder;

    MissionSaver(Activity activity) {
        this(activity, false);
    }

    //Creates MissionSaver.  Uses GSon to convert missions to JSon data.
    MissionSaver(Activity activity, boolean addDefault) {
        savedMissions = new ArrayList<>();
        attachedActivity = activity;
        this.addDefault = addDefault;
        //Used to create GSon, which does class to Json conversion.
        gBuilder = new GsonBuilder();
        //Create custom type adapters for missions and tasks, so Gson knows how to get
        //the subclass from the parent mission or task class.
        gBuilder.registerTypeAdapter(Mission.class, new MissionSaveAdapter());
        gBuilder.registerTypeAdapter(Task.class, new MissionSaveAdapter());
    }

    int size() {
        return savedMissions.size();
    }

    Mission getMissionAt(int pos) {
        return savedMissions.get(pos);
    }

    //Loads mission list from json files in specified directory
    void unloadMissions() {
        //Directory to save json files in.  Current directory is an external directory in the app's data folders.
        File directory = attachedActivity.getExternalFilesDir(null);
        if (directory != null) {
            File[] files = directory.listFiles();
            //Populate the list with default missions if enabled.
            if (addDefault) {
                for (int i = 0; i < NUM_DEFAULT; i++) {
                    String des = "Drone flies.";
                    savedMissions.add(new TestMission(String.format(Locale.getDefault(), "Default Mission %d", DEFAULT_ID++)));
                }
            }
            for (File f : files) {
                Mission mission = loadFile(f);
                if (mission != null) {
                    savedMissions.add(mission);
                }
            }
        } else {
            throw new RuntimeException("Could not load missions from default directory");
        }
    }

    //Convert mission to json using GSon
    void saveMission(Mission mission) {
        try {
            Gson gson = gBuilder.create();
            Writer output = null;
            File file = new File(attachedActivity.getExternalFilesDir(null), String.format("%s.json", mission.getTitle()));
            output = new BufferedWriter(new FileWriter(file));
            output.write(gson.toJson(mission, Mission.class));
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        savedMissions.add(mission);
    }

    //Loads a json file and converts it to a mission.
    private Mission loadFile(File file) {
        Mission missionFile = null;
        try {
            InputStream is = new FileInputStream(file.getPath());
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonStr = new String(buffer);
            Gson gson = gBuilder.create();
            missionFile = gson.fromJson(jsonStr, Mission.class);
        } catch (IOException ie) {
            ie.printStackTrace();
        }
        return missionFile;
    }
}
