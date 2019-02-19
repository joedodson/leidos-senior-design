package com.leidossd.dronecontrollerapp;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class WaypointFragment extends Fragment {

    private enum Direction {
        NW, N, NE,
        W, E,
        SW, S, SE
    };

    private Direction direction;
    private String directionMessage;
    private ImageButton currentButton;

    private WaypointFragmentListener waypointFragmentListener;
    private View.OnClickListener gridSelectListener;

    private TextView title;
    private TextView description;
    private ImageView droneImage;
    private Button createButton;

    private TextView noPressed;
    private boolean pressedOnce = false;

    public WaypointFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_waypoint, container, false);

        int[] buttons = {R.id.button_nw, R.id.button_n, R.id.button_ne,
                R.id.button_w, R.id.button_e,
                R.id.button_sw, R.id.button_s, R.id.button_se};

        title = view.findViewById(R.id.mission_name);
        description = view.findViewById(R.id.mission_description);
        droneImage = view.findViewById(R.id.drone_image);
        createButton = view.findViewById(R.id.button_create);

        gridSelectListener = new View.OnClickListener() {
            public void onClick(View view) {
                int id = view.getId();
                ImageButton gridButton = (ImageButton) view;
                if (direction != null) {
                    changeView();
                }
                switch (id) {
                    case R.id.button_nw:
                        gridButton.setImageResource(R.drawable.ic_arrow_nw_l);
                        direction = Direction.NW;
                        break;
                    case R.id.button_n:
                        gridButton.setImageResource(R.drawable.ic_arrow_up_l);
                        direction = Direction.N;
                        break;
                    case R.id.button_ne:
                        gridButton.setImageResource(R.drawable.ic_arrow_ne_l);
                        direction = Direction.NE;
                        break;
                    case R.id.button_w:
                        gridButton.setImageResource(R.drawable.ic_arrow_left_l);
                        direction = Direction.W;
                        break;
                    case R.id.button_e:
                        gridButton.setImageResource(R.drawable.ic_arrow_right_l);
                        direction = Direction.E;
                        break;
                    case R.id.button_sw:
                        gridButton.setImageResource(R.drawable.ic_arrow_sw_l);
                        direction = Direction.SW;
                        break;
                    case R.id.button_s:
                        gridButton.setImageResource(R.drawable.ic_arrow_down_l);
                        direction = Direction.S;
                        break;
                    case R.id.button_se:
                        gridButton.setImageResource(R.drawable.ic_arrow_se_l);
                        direction = Direction.SE;
                        break;
                    default:
                        return;
                }
                currentButton = gridButton;
                if (!pressedOnce) {
                    title.setVisibility(View.VISIBLE);
                    description.setText("Here is some text that hopefully passes onto here somehow.");
                    droneImage.setVisibility(View.VISIBLE);
                    createButton.setVisibility(View.VISIBLE);
                    pressedOnce = true;
                }
            }
        };

        for (int i : buttons){
            view.findViewById(i).setOnClickListener(gridSelectListener);
        }

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (waypointFragmentListener != null) {
            waypointFragmentListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof WaypointFragmentListener) {
            waypointFragmentListener = (WaypointFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement WaypointFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        waypointFragmentListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface WaypointFragmentListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void changeView() {
        switch(direction) {
            case NW:
                currentButton.setImageResource(R.drawable.ic_arrow_nw);
                break;
            case N:
                currentButton.setImageResource(R.drawable.ic_arrow_up);
                break;
            case NE:
                currentButton.setImageResource(R.drawable.ic_arrow_ne);
                break;
            case W:
                currentButton.setImageResource(R.drawable.ic_arrow_left);
                break;
            case E:
                currentButton.setImageResource(R.drawable.ic_arrow_right);
                break;
            case SW:
                currentButton.setImageResource(R.drawable.ic_arrow_sw);
                break;
            case S:
                currentButton.setImageResource(R.drawable.ic_arrow_down);
                break;
            case SE:
                currentButton.setImageResource(R.drawable.ic_arrow_se);
                break;
        }
    }
}
