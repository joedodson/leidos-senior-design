package com.leidossd.dronecontrollerapp.droneconnection.wireless;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leidossd.dronecontrollerapp.R;


public class WirelessPage1Fragment extends Fragment {
    public WirelessPage1Fragment() {
    }

    public static WirelessPage1Fragment newInstance() {
        return new WirelessPage1Fragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wireless_page1, container, false);
    }
}
