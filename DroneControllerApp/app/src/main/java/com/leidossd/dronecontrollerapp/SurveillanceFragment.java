package com.leidossd.dronecontrollerapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SurveillanceFragment extends Fragment {
    private View.OnClickListener createButtonListener;
    private MissionCreateListener surveillanceFragmentListener;

    private RecyclerView sectionView;
    private RecyclerView.LayoutManager layoutManager;
    private MenuAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createButtonListener = new View.OnClickListener() {
            public void onClick(View view) { }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_surveillance, container, false);
        sectionView = view.findViewById(R.id.properties);
        sectionView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        sectionView.setLayoutManager(layoutManager);
        adapter = new MenuAdapter(this);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MissionCreateListener) {
            surveillanceFragmentListener = (MissionCreateListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement MissionCreateListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        surveillanceFragmentListener = null;
    }
}
