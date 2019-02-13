package com.leidossd.dronecontrollerapp.droneconnection;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leidossd.dronecontrollerapp.R;

/**
 * User chooses wired or wireless connection type.
 * Designed to be used in ViewPager.
 */
public class ChooseConnectTypeFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";

    private fragmentInteractionListener fragmentInteractionListener;
    private View.OnClickListener clickListener;

    // TODO: Rename and change types of parameters
    private String mParam1;

    public ChooseConnectTypeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ChooseConnectTypeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChooseConnectTypeFragment newInstance(String param1) {
        ChooseConnectTypeFragment fragment = new ChooseConnectTypeFragment();
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
                    case R.id.btn_connect_type_wired:
                        fragmentInteractionListener.onConnectTypeDecision(ConnectType.WIRED);
                        break;
                    case R.id.btn_connect_type_wireless:
                        fragmentInteractionListener.onConnectTypeDecision(ConnectType.WIRELESS);
                        break;
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_choose_connect_type, container, false);

        view.findViewById(R.id.btn_connect_type_wired).setOnClickListener(clickListener);
        view.findViewById(R.id.btn_connect_type_wireless).setOnClickListener(clickListener);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ChooseConnectTypeFragment.fragmentInteractionListener) {
            fragmentInteractionListener = (ChooseConnectTypeFragment.fragmentInteractionListener) context;
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
        void onConnectTypeDecision(ConnectType decision);
    }

    protected enum ConnectType {
        WIRED, WIRELESS
    }
}
