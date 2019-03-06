package com.leidossd.dronecontrollerapp.compass;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.leidossd.dronecontrollerapp.R;

public class CompassStartPageFragment extends Fragment {

    Button exitButton;
    Button startButton;

    public CompassStartPageFragment() {
    }

    public static CompassStartPageFragment newInstance(String param1) {
        return new CompassStartPageFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_compass_startpage, container, false);

        startButton = view.findViewById(R.id.btn_compass_start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((StartPage) getActivity()).startCalibration();
            }
        });

        exitButton = view.findViewById(R.id.btn_compass_exit);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((StartPage) getActivity()).exitCalibration();
            }
        });

        return view;
    }

    interface StartPage {
        void startCalibration();
        void exitCalibration();
    }
}