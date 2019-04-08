package com.leidossd.dronecontrollerapp.missions.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.leidossd.djiwrapper.Coordinate;
import com.leidossd.dronecontrollerapp.R;
import com.leidossd.dronecontrollerapp.missions.SurveillanceMission;

import java.util.ArrayList;

import static com.leidossd.dronecontrollerapp.MainApplication.showToast;

public class SurveillanceFragment extends Fragment implements MissionMenuAdapter.MenuListener, OnMapReadyCallback {
    private static final int WAYPOINT = 1, CAMERA_ANGLE = 2;
    private static final float DEFAULT_ZOOM = 10.0f;

    private View.OnClickListener createButtonListener;
    private MissionCreateListener surveillanceFragmentListener;
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

    private RecyclerView sectionView;
    private RecyclerView.LayoutManager layoutManager;
    private MissionMenuAdapter adapter;
    private TextView angleTextInfo;
    private EditText angleText;
    private CheckBox saveCheckbox;
    private Button createButton;
    private TextView description;
    private ImageView droneImage;

    private GoogleMap googleMap;
    private MapView mapView;
    private FusedLocationProviderClient fusedLocationClient;
    private Location location;
    private Coordinate destination = null;
    private int locationPerimission;
    int curMenuSelection = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createButtonListener = new View.OnClickListener() {
            public void onClick(View view) {
                if(destination != null){
                    float cameraAngle = Float.parseFloat(angleText.getText().toString());
                    SurveillanceMission sm = new SurveillanceMission("New Mission", destination, cameraAngle);
                    surveillanceFragmentListener.createMission(sm, saveCheckbox.isChecked());
                } else {
                    showToast("Please Enter a Location.");
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_surveillance, container, false);

        angleTextInfo = view.findViewById(R.id.text_angle);
        angleText = view.findViewById(R.id.angle_box);
        saveCheckbox = view.findViewById(R.id.mission_save);
        createButton = view.findViewById(R.id.button_create);
        createButton.setOnClickListener(createButtonListener);
        description = view.findViewById(R.id.mission_description);
        droneImage = view.findViewById(R.id.drone_image);

        sectionView = view.findViewById(R.id.properties);
        sectionView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        sectionView.setLayoutManager(layoutManager);

        ArrayList<Pair<String, String>> menuOptions = new ArrayList<>();
        menuOptions.add(new Pair<>("Waypoint", "Select Waypoint to move to."));
        menuOptions.add(new Pair<>("Camera Angle", "Set camera angle for surveillance."));

        sectionView.addItemDecoration(new DividerItemDecoration(getActivity()));
        adapter = new MissionMenuAdapter(this, menuOptions);
        sectionView.setAdapter(adapter);

        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationPerimission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);

        updateFragment();
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
    public void onMapReady(GoogleMap gMap) {
        this.googleMap = gMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);

        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setZoomGesturesEnabled(false);
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(point));

                Location pLocation = new Location("");
                pLocation.setLatitude(point.latitude);
                pLocation.setLongitude(point.longitude);

                destination = getDistance(location, pLocation);
                showToast("X: " + destination.getX() + "Y: " + destination.getY());
            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return true;
            }
        });

        updateMap();
        getLocation();
    }

    private Coordinate getDistance(Location l1, Location l2){
        Point p = googleMap.getProjection().toScreenLocation(new LatLng(l1.getLatitude(), l1.getLongitude()));
        Point q = googleMap.getProjection().toScreenLocation(new LatLng(l2.getLatitude(), l2.getLongitude()));
        float x = (float)(q.x-p.x)/p.x;
        float y = (float)-(q.y-p.y)/p.y;

        return new Coordinate(x, y,0);
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
                        } else {
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0,0), DEFAULT_ZOOM));
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
            } else {
                googleMap.setMyLocationEnabled(false);
                location = null;
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
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
        surveillanceFragmentListener = null;
    }

    @Override
    public void menuClicked(int pos) {
        if(pos == curMenuSelection){
            curMenuSelection = 0;
        } else {
            curMenuSelection = pos;
        }
        updateFragment();
    }

    private void updateFragment(){
        if(curMenuSelection == WAYPOINT){
            mapView.setVisibility(View.VISIBLE);
            angleText.setVisibility(View.INVISIBLE);
            angleTextInfo.setVisibility(View.INVISIBLE);
            description.setVisibility(View.INVISIBLE);
            droneImage.setVisibility(View.INVISIBLE);
        } else if (curMenuSelection == CAMERA_ANGLE){
            mapView.setVisibility(View.INVISIBLE);
            angleText.setVisibility(View.VISIBLE);
            angleTextInfo.setVisibility(View.VISIBLE);
            description.setVisibility(View.INVISIBLE);
            droneImage.setVisibility(View.INVISIBLE);
        } else {
            mapView.setVisibility(View.INVISIBLE);
            angleText.setVisibility(View.INVISIBLE);
            angleTextInfo.setVisibility(View.INVISIBLE);
            description.setVisibility(View.VISIBLE);
            droneImage.setVisibility(View.VISIBLE);
        }
    }

    private class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private Drawable divider;

        DividerItemDecoration(Context context) {
            final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
            divider = styledAttributes.getDrawable(0);
            styledAttributes.recycle();
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + divider.getIntrinsicHeight();

                divider.setBounds(left, top, right, bottom);
                divider.draw(c);
            }
        }
    }
}
