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
import static com.leidossd.utils.MenuAction.OPEN_COMPASS;
import static com.leidossd.utils.MenuAction.OPEN_DEVELOPER;
import static com.leidossd.utils.MenuAction.OPEN_GRID_VIEW;
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
                        case R.id.main_menu_compass:
                            action = OPEN_COMPASS;
                            break;
                        case R.id.main_menu_grid:
                            action = OPEN_GRID_VIEW;
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

        // All menu item IDs need to be listed here
        int[] viewIds = {
                R.id.main_menu_close,
                R.id.main_menu_mission,
                R.id.main_menu_developer,
                R.id. main_menu_simulator,
                R.id.main_menu_compass,
                R.id.main_menu_grid,
                R.id.main_menu_settings,
        };

        // Set listener for all menu items
        for (int id : viewIds) {
            view.findViewById(id).setOnClickListener(menuSelectListener);
        }

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