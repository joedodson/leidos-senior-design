package com.leidossd.dronecontrollerapp.compass;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.leidossd.dronecontrollerapp.R;

public class CompassPage1Fragment extends Fragment {

    Button cancelButton;

    public CompassPage1Fragment() {
    }

    public static CompassPage1Fragment newInstance(String param1) {
        return new CompassPage1Fragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_compass_page1, container, false);
        cancelButton = view.findViewById(R.id.btn_compass_pg1_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Page1) getActivity()).cancelPage1();
            }
        });
        return view;
    }

    interface Page1 {
        void cancelPage1();
    }
}