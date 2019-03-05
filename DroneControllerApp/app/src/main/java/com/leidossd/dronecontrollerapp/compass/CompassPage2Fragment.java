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

    interface Page2 {
        void cancelPage2();
    }
}