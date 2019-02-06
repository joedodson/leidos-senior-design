package com.leidossd.dronecontrollerapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// Dummy class for testing GridParentActivity
public class GridFragment extends Fragment {

    private fragmentInteractionListener fragmentInteractionListener;
    private View.OnClickListener buttonClickListener;

    public GridFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        buttonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fragmentInteractionListener != null) {
                    fragmentInteractionListener.sendInput("Data from GridFragment");
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_grid, container, false);

        view.findViewById(R.id.button_grid_test).setOnClickListener(buttonClickListener);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof fragmentInteractionListener) {
            fragmentInteractionListener = (fragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement fragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentInteractionListener = null;
    }

    public interface fragmentInteractionListener {
        void sendInput(String s);
    }
}
