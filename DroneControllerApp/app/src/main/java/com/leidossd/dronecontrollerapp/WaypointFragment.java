package com.leidossd.dronecontrollerapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.leidossd.djiwrapper.Coordinate;
import com.leidossd.dronecontrollerapp.missions.SpecificMission;
import com.leidossd.utils.Direction;

import static com.leidossd.dronecontrollerapp.MainApplication.showToast;

public class WaypointFragment extends Fragment implements OnMapReadyCallback {
    private static final float DEFAULT_ZOOM = 21.0f;
    private static final String TAG = "WaypointFragment";

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

    private boolean mLocationPermissionGranted;

    private TextView noPressed;
    private boolean pressedOnce = false;
    private Location location;
    int locationPerimission;

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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationPerimission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(point));

                Location pLocation = new Location("");
                pLocation.setLatitude(point.latitude);
                pLocation.setLongitude(point.longitude);

                Coordinate d = getDistance(location, pLocation);

                if (!pressedOnce) {
                    title.setVisibility(View.VISIBLE);
                    description.setVisibility(View.VISIBLE);
                    textDir.setVisibility(View.VISIBLE);
                    droneImage.setVisibility(View.VISIBLE);
                    createButton.setVisibility(View.VISIBLE);
                    missionName.setVisibility(View.VISIBLE);
                    saveCheckbox.setVisibility(View.VISIBLE);
                    noPressed.setVisibility(View.INVISIBLE);
                    pressedOnce = true;
                }
            }
        });

        updateMap();
        getLocation();
    }

    private Coordinate getDistance(Location l1, Location l2){
        final int RADIUS = 6378137;
        double la1 = Math.toRadians(l1.getLatitude());
        double la2 = Math.toRadians(l2.getLatitude());
        double lo1 = Math.toRadians(l1.getLongitude());
        double lo2 = Math.toRadians(l2.getLongitude());
        double x = RADIUS*Math.cos(la2)*Math.cos(lo2) - RADIUS*Math.cos(la1)*Math.cos(lo1);
        double y = RADIUS*Math.cos(la2)*Math.sin(lo2) - RADIUS*Math.cos(la1)*Math.sin(lo1);
        double z = RADIUS*Math.sin(la2) - RADIUS*Math.sin(la1);
        double ret = Math.sqrt(Math.pow(x, 2)+Math.pow(y, 2)+Math.pow(z, 2));
        showToast("Distance: " + ret + "\nX: " + x + "\nY: " + y);
        return new Coordinate((float)x, (float)y, 0);
    }

    private void getLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPerimission == PackageManager.PERMISSION_GRANTED) {
                Task locationResult = fusedLocationClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            location = (Location)task.getResult();
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(location.getLatitude(),
                                            location.getLongitude()), DEFAULT_ZOOM));
                            showToast("Lat: " + location.getLatitude() +
                                "Long:" + location.getLongitude());
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0,0), DEFAULT_ZOOM));
                            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void updateMap() {
        if (googleMap == null) {
            return;
        }
        try {
            if (locationPerimission == PackageManager.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                googleMap.setMyLocationEnabled(false);
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                location = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {

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
