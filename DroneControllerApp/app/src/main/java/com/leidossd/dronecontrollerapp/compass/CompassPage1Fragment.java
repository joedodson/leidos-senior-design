package com.leidossd.dronecontrollerapp.compass;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leidossd.dronecontrollerapp.R;

public class CompassPage1Fragment extends Fragment {
    public CompassPage1Fragment() { }

    public static CompassPage1Fragment newInstance(String param1) {
        return new CompassPage1Fragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compass_page1, container, false);
    }
}