package com.leidossd.dronecontrollerapp.droneconnection;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leidossd.dronecontrollerapp.R;

/**
 * User chooses to go through connection or skip to main screen.
 * Designed to be used in ViewPager.
 */
public class ChooseToConnectFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";

    private View.OnClickListener clickListener;
    private fragmentInteractionListener fragmentInteractionListener;

    // TODO: Rename and change types of parameters
    private String mParam1;

    public ChooseToConnectFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ChooseToConnectFragment.
     */
    public static ChooseToConnectFragment newInstance(String param1) {
        ChooseToConnectFragment fragment = new ChooseToConnectFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }

        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_choose_connect_connect:
                        fragmentInteractionListener.onConnectDecision(ConnectDecision.CONNECT);
                        break;
                    case R.id.btn_choose_connect_skip:
                        fragmentInteractionListener.onConnectDecision(ConnectDecision.SKIP);
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_choose_to_connect, container, false);

        view.findViewById(R.id.btn_choose_connect_connect).setOnClickListener(clickListener);
        view.findViewById(R.id.btn_choose_connect_skip).setOnClickListener(clickListener);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ChooseToConnectFragment.fragmentInteractionListener) {
            fragmentInteractionListener = (ChooseToConnectFragment.fragmentInteractionListener) context;
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
        void onConnectDecision(ConnectDecision action);
    }

    protected enum ConnectDecision {
        CONNECT, SKIP
    }
}
