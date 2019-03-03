package com.leidossd.dronecontrollerapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.leidossd.djiwrapper.Mission;
import com.leidossd.utils.Direction;
import com.leidossd.utils.MissionAction;

public class WaypointFragment extends Fragment {

    private Direction direction;
    private String directionMessage;
    private ImageButton currentButton;

    private MissionCreateListener waypointFragmentListener;
    private View.OnClickListener gridSelectListener;
    private View.OnClickListener createButtonListener;

    private TextView title;
    private TextView description;
    private TextView textDir;
    private ImageView droneImage;
    private Button createButton;

    private TextView noPressed;
    private boolean pressedOnce = false;

    public WaypointFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                textDir.setText("Direction to go: " + direction.getDir());
                if (!pressedOnce) {
                    title.setVisibility(View.VISIBLE);
                    description.setVisibility(View.VISIBLE);
                    textDir.setVisibility(View.VISIBLE);
                    droneImage.setVisibility(View.VISIBLE);
                    createButton.setVisibility(View.VISIBLE);
                    noPressed.setVisibility(View.INVISIBLE);
                    pressedOnce = true;
                }
            }
        };

        createButtonListener = new View.OnClickListener() {
            public void onClick(View view) {
                Mission mission = null;
                waypointFragmentListener.createMission(mission);
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_waypoint, container, false);

        int[] buttons = {R.id.button_nw, R.id.button_n, R.id.button_ne,
                R.id.button_w, R.id.button_e,
                R.id.button_sw, R.id.button_s, R.id.button_se};

        for (int i : buttons){
            view.findViewById(i).setOnClickListener(gridSelectListener);
        }

        title = view.findViewById(R.id.mission_name);
        description = view.findViewById(R.id.mission_description);
        textDir = view.findViewById(R.id.mission_direction);
        droneImage = view.findViewById(R.id.drone_image);
        createButton = view.findViewById(R.id.button_create);
        noPressed = view.findViewById(R.id.text_nopressed);
        createButton.setOnClickListener(createButtonListener);

        title.setVisibility(View.INVISIBLE);
        description.setVisibility(View.INVISIBLE);
        textDir.setVisibility(View.INVISIBLE);
        droneImage.setVisibility(View.INVISIBLE);
        createButton.setVisibility(View.INVISIBLE);
        noPressed.setVisibility(View.VISIBLE);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MissionCreateListener) {
            waypointFragmentListener = (MissionCreateListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement MissionCreateListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        waypointFragmentListener = null;
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
