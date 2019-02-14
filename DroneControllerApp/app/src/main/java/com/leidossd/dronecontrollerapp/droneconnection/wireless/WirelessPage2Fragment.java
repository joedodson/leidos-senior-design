package com.leidossd.dronecontrollerapp.droneconnection.wireless;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leidossd.dronecontrollerapp.R;

public class WirelessPage2Fragment extends Fragment {
    public WirelessPage2Fragment() { }

    public static WirelessPage2Fragment newInstance(String param1) {
        return new WirelessPage2Fragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wireless_page2, container, false);
    }
}
