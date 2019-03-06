package com.leidossd.dronecontrollerapp.compass;


import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.leidossd.dronecontrollerapp.R;


public class CompassPage2Fragment extends Fragment {

    Button cancelButton;

    public CompassPage2Fragment() {
    }

    public static CompassPage2Fragment newInstance(String param1) {
        return new CompassPage2Fragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_compass_page2, container, false);

        cancelButton = view.findViewById(R.id.btn_compass_pg2_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Page2) getActivity()).cancelPage2();
            }
        });

        return view;
    }

    interface Page2 {
        void cancelPage2();
    }
}