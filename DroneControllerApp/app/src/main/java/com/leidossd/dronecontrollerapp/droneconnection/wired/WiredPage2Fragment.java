package com.leidossd.dronecontrollerapp.droneconnection.wired;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leidossd.dronecontrollerapp.R;

public class WiredPage2Fragment extends Fragment {
    public WiredPage2Fragment() { }

    public static WiredPage2Fragment newInstance(String param1) {
        return new WiredPage2Fragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wired_page2, container, false);
    }
}
