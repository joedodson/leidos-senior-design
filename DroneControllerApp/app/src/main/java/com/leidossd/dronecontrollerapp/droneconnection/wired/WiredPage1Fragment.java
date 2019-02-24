package com.leidossd.dronecontrollerapp.droneconnection.wired;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leidossd.dronecontrollerapp.R;

public class WiredPage1Fragment extends Fragment {
    public WiredPage1Fragment() { }

    public static WiredPage1Fragment newInstance(String param1) {
        return new WiredPage1Fragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wired_page1, container, false);
    }
}
