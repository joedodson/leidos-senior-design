package com.leidossd.dronecontrollerapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.leidossd.utils.MenuAction;

import static com.leidossd.utils.MenuAction.CLOSE_MENU;
import static com.leidossd.utils.MenuAction.OPEN_DEVELOPER;
import static com.leidossd.utils.MenuAction.OPEN_MISSIONS;
import static com.leidossd.utils.MenuAction.OPEN_SETTINGS;
import static com.leidossd.utils.MenuAction.OPEN_SIMULATOR;


public class MenuFragment extends Fragment {

    private fragmentInteractionListener fragmentInteractionListener;

    private View.OnClickListener menuSelectListener;

    public MenuFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        menuSelectListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fragmentInteractionListener != null) {
                    MenuAction action = null;

                    switch(view.getId()) {
                        case R.id.main_menu_mission:
                            action = OPEN_MISSIONS;
                            break;
                        case R.id.main_menu_developer:
                            action = OPEN_DEVELOPER;
                            break;
                        case R.id.main_menu_simulator:
                            action = OPEN_SIMULATOR;
                            break;
                        case R.id.main_menu_settings:
                            action = OPEN_SETTINGS;
                            break;
                        default:
                            action = CLOSE_MENU;
                    }
                    fragmentInteractionListener.onMenuSelect(action);
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        view.findViewById(R.id.main_menu_close).setOnClickListener(menuSelectListener);
        view.findViewById(R.id.main_menu_mission).setOnClickListener(menuSelectListener);
        view.findViewById(R.id.main_menu_developer).setOnClickListener(menuSelectListener);
        view.findViewById(R.id.main_menu_simulator).setOnClickListener(menuSelectListener);
        view.findViewById(R.id.main_menu_settings).setOnClickListener(menuSelectListener);

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
        void onMenuSelect(MenuAction action);
    }
}