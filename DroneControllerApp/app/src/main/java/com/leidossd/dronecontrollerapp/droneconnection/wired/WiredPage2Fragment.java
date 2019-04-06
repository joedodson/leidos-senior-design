package com.leidossd.dronecontrollerapp.droneconnection.wired;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.leidossd.dronecontrollerapp.MainActivity;
import com.leidossd.dronecontrollerapp.R;
import com.leidossd.utils.DroneConnectionStatus;

import dji.sdk.sdkmanager.DJISDKManager;

import static com.leidossd.utils.IntentAction.CONNECTION_CHANGE;

public class WiredPage2Fragment extends Fragment {

    Button testConnectButton;
    Handler handler;

    LocalBroadcastManager localBroadcastManager;

    BroadcastReceiver connectionChangeReceiver;

    public WiredPage2Fragment() { }

    public static WiredPage2Fragment newInstance(String param1) {
        return new WiredPage2Fragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wired_page2, container, false);

        handler = new Handler();

        if(getActivity() != null) {
            localBroadcastManager = LocalBroadcastManager.getInstance(getActivity().getApplicationContext());

            connectionChangeReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent connectionChangeIntent) {
                    handler.removeCallbacksAndMessages(null);

                    String droneStatus = connectionChangeIntent.getStringExtra(CONNECTION_CHANGE.getResultKey());
                    if(droneStatus.equals(DroneConnectionStatus.DRONE_CONNECTED.toString())) {

                        testConnectButton.setText(getString(R.string.activity_droneconnection_connectSuccess));
                        testConnectButton.setEnabled(true);
                        testConnectButton.setOnClickListener(v -> {
                            startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
                            getActivity().finish();
                        });
                    }
                }
            };

            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(connectionChangeReceiver, new IntentFilter(CONNECTION_CHANGE.getActionString()));
        }

        testConnectButton = view.findViewById(R.id.btn_frag_wired_test_connect);
        testConnectButton.setOnClickListener(v -> {
            DJISDKManager.getInstance().startConnectionToProduct();
            testConnectButton.setText(getString(R.string.activity_droneconnection_connecting));
            testConnectButton.setEnabled(false);

            handler.postDelayed(() -> {
                testConnectButton.setText(getString(R.string.activity_droneconnection_connectError));
                testConnectButton.setEnabled(true);
            }, 10000);
        });

        return view;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        localBroadcastManager.unregisterReceiver(connectionChangeReceiver);
        super.onDestroy();
    }
}
