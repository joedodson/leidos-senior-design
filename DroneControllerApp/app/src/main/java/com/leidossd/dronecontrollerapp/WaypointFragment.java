package com.leidossd.dronecontrollerapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.leidossd.dronecontrollerapp.missions.SpecificMission;
import com.leidossd.utils.Direction;

public class WaypointFragment extends Fragment implements OnMapReadyCallback{

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
    private EditText missionName;
    private CheckBox saveCheckbox;
    private GoogleMap googleMap;
    private MapView mapView;
    private FusedLocationProviderClient fusedLocationClient;



    private TextView noPressed;
    private boolean pressedOnce = false;

    public WaypointFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createButtonListener = new View.OnClickListener() {
            public void onClick(View view) {
                SpecificMission mission;
                String mName = missionName.getText().toString();
                if(mName.equals(""))
                    mission = new SpecificMission("Waypoint Mission");
                else
                    mission = new SpecificMission(mName);
                waypointFragmentListener.createMission(mission, saveCheckbox.isChecked());
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_waypoint, container, false);

        title = view.findViewById(R.id.mission_type);
        description = view.findViewById(R.id.mission_description);
        textDir = view.findViewById(R.id.mission_direction);
        droneImage = view.findViewById(R.id.drone_image);
        createButton = view.findViewById(R.id.button_create);
        noPressed = view.findViewById(R.id.text_nopressed);
        missionName = view.findViewById(R.id.mission_name);
        saveCheckbox = view.findViewById(R.id.mission_save);
        createButton.setOnClickListener(createButtonListener);

        title.setVisibility(View.INVISIBLE);
        description.setVisibility(View.INVISIBLE);
        textDir.setVisibility(View.INVISIBLE);
        droneImage.setVisibility(View.INVISIBLE);
        createButton.setVisibility(View.INVISIBLE);
        missionName.setVisibility(View.INVISIBLE);
        saveCheckbox.setVisibility(View.INVISIBLE);
        noPressed.setVisibility(View.VISIBLE);

        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
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
    public void onResume(){
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
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
