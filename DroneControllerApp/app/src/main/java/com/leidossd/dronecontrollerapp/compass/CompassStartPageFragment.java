package com.leidossd.dronecontrollerapp.compass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.leidossd.djiwrapper.FlightControllerWrapper;
import com.leidossd.dronecontrollerapp.MainActivity;
import com.leidossd.dronecontrollerapp.MenuFragment;
import com.leidossd.dronecontrollerapp.R;

import dji.sdk.sdkmanager.DJISDKManager;


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

        //handler = new Handler();

        //if (getActivity() != null) {
        //    localBroadcastManager = LocalBroadcastManager.getInstance(getActivity().getApplicationContext());

        //    connectionChangeReceiver = new BroadcastReceiver() {
        //        @Override
        //        public void onReceive(Context context, Intent connectionChangeIntent) {
        //            handler.removeCallbacksAndMessages(null);

        //            if (!FlightControllerWrapper.getInstance().compassHasError()) {

        //                testCompassButton.setText(getString(R.string.activity_compass_calib_success));
        //                testCompassButton.setEnabled(true);
        //                testCompassButton.setOnClickListener(v -> {
        //                    startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
        //                    getActivity().finish();
        //                });
        //            }
        //        }
        //    };

        //    LocalBroadcastManager.getInstance(getActivity()).registerReceiver(connectionChangeReceiver, new IntentFilter(CONNECTION_CHANGE.getActionString()));
        //}

        //testCompassButton = view.findViewById(R.id.btn_frag_compass_calib_start);
        //testCompassButton.setOnClickListener(v -> {
        //    DJISDKManager.getInstance().startConnectionToProduct();
        //    testCompassButton.setText(getString(R.string.activity_droneconnection_connecting));
        //    testCompassButton.setEnabled(false);

        //    handler.postDelayed(() -> {
        //        testConnectButton.setText(getString(R.string.activity_droneconnection_connectError));
        //        testConnectButton.setEnabled(true);
        //    }, 10000);
        //});

        return view;
    }

    interface StartPage {
        void startCalibration();
        void exitCalibration();
    }
}