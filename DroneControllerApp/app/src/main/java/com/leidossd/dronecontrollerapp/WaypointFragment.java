package com.leidossd.dronecontrollerapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.leidossd.djiwrapper.Coordinate;
import com.leidossd.djiwrapper.FlightControllerWrapper;
import com.leidossd.dronecontrollerapp.missions.SpecificMission;
import com.leidossd.dronecontrollerapp.missions.WaypointMission;
import com.leidossd.utils.Direction;

import java.io.ByteArrayOutputStream;

import static com.leidossd.dronecontrollerapp.MainApplication.showToast;

public class WaypointFragment extends Fragment implements OnMapReadyCallback {
    private static final float DEFAULT_ZOOM = 21.0f;
    private static final int TILE_SIZE = 256;

    private Direction direction;
    private String directionMessage;
    private ImageButton currentButton;

    private MissionCreateListener waypointFragmentListener;
    private View.OnClickListener gridSelectListener;
    private View.OnClickListener createButtonListener;

    private TextView title;
    private TextView description;
    private ImageView droneImage;
    private Button createButton;
    private EditText missionName;
    private CheckBox saveCheckbox;
    private TextView positionText;
    private GoogleMap googleMap;
    private MapView mapView;
    private FusedLocationProviderClient fusedLocationClient;

    private boolean mLocationPermissionGranted;

    private TextView noPressed;
    private boolean pressedOnce = false;
    private Location location;
    private Coordinate destination = null;
    private int locationPerimission;

    private Coordinate coordinate;

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
                        coordinate = new Coordinate(-1,1,0);
                        direction = Direction.NW;
                        break;
                    case R.id.button_n:
                        coordinate = new Coordinate(0,1,0);
                        gridButton.setImageResource(R.drawable.ic_arrow_up_l);
                        direction = Direction.N;
                        break;
                    case R.id.button_ne:
                        coordinate = new Coordinate(1,1,0);
                        gridButton.setImageResource(R.drawable.ic_arrow_ne_l);
                        direction = Direction.NE;
                        break;
                    case R.id.button_w:
                        coordinate = new Coordinate(-1,0,0);
                        gridButton.setImageResource(R.drawable.ic_arrow_left_l);
                        direction = Direction.W;
                        break;
                    case R.id.button_e:
                        coordinate = new Coordinate(1,0,0);
                        gridButton.setImageResource(R.drawable.ic_arrow_right_l);
                        direction = Direction.E;
                        break;
                    case R.id.button_sw:
                        coordinate = new Coordinate(-1,-1,0);
                        gridButton.setImageResource(R.drawable.ic_arrow_sw_l);
                        direction = Direction.SW;
                        break;
                    case R.id.button_s:
                        coordinate = new Coordinate(0,-1,0);
                        gridButton.setImageResource(R.drawable.ic_arrow_down_l);
                        direction = Direction.S;
                        break;
                    case R.id.button_se:
                        coordinate = new Coordinate(1,-1,0);
                        gridButton.setImageResource(R.drawable.ic_arrow_se_l);
                        direction = Direction.SE;
                        break;
                    default:
                        return;
                }
                currentButton = gridButton;
                textDir.setText(String.format("Direction to go: %s", direction.getDir()));
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
        };

        createButtonListener = new View.OnClickListener() {
            public void onClick(View view) {
                if(coordinate != null) {
                    coordinate = Coordinate.sum(FlightControllerWrapper.getInstance().getPosition(), coordinate);
                    waypointFragmentListener.createMission(new WaypointMission(coordinate), saveCheckbox.isChecked());
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_waypoint, container, false);

        title = view.findViewById(R.id.mission_type);
        description = view.findViewById(R.id.mission_description);
        droneImage = view.findViewById(R.id.drone_image);
        createButton = view.findViewById(R.id.button_create);
        noPressed = view.findViewById(R.id.text_nopressed);
        missionName = view.findViewById(R.id.mission_name);
        saveCheckbox = view.findViewById(R.id.mission_save);
        positionText = view.findViewById(R.id.text_position);
        createButton.setOnClickListener(createButtonListener);

        title.setVisibility(View.INVISIBLE);
        description.setVisibility(View.INVISIBLE);
        droneImage.setVisibility(View.INVISIBLE);
        createButton.setVisibility(View.INVISIBLE);
        missionName.setVisibility(View.INVISIBLE);
        saveCheckbox.setVisibility(View.INVISIBLE);
        positionText.setVisibility(View.INVISIBLE);
        noPressed.setVisibility(View.VISIBLE);

        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationPerimission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        this.googleMap = gMap;

        googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);

//        googleMap.getUiSettings().setRotateGesturesEnabled(false);
//        googleMap.getUiSettings().setZoomControlsEnabled(false);
//        googleMap.getUiSettings().setZoomGesturesEnabled(false);
//        googleMap.getUiSettings().setScrollGesturesEnabled(false);
//        googleMap.getUiSettings().setMyLocationButtonEnabled(false);

        TileProvider tileProvider = new TileProvider() {
            @Override
            public Tile getTile(int i, int i1, int i2) {
                Drawable d = ContextCompat.getDrawable(getContext(), R.drawable.grid_overlay);
                Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
                byte[] bitmapData = stream.toByteArray();
                return new Tile(TILE_SIZE, TILE_SIZE, bitmapData);
            }
        };

        googleMap.addTileOverlay(new TileOverlayOptions()
                .tileProvider(tileProvider));

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(point));

                Location pLocation = new Location("");
                pLocation.setLatitude(point.latitude);
                pLocation.setLongitude(point.longitude);

                destination = getDistance(location, pLocation);

                if (!pressedOnce) {
                    title.setVisibility(View.VISIBLE);
                    description.setVisibility(View.VISIBLE);
                    droneImage.setVisibility(View.VISIBLE);
                    createButton.setVisibility(View.VISIBLE);
                    missionName.setVisibility(View.VISIBLE);
                    saveCheckbox.setVisibility(View.VISIBLE);
                    positionText.setVisibility(View.VISIBLE);
                    noPressed.setVisibility(View.INVISIBLE);
                    pressedOnce = true;
                }

                positionText.setText(String.format("X: %f, Y: %f, Z: %f", destination.getX(), destination.getY(), destination.getZ()));
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
                            showToast("Lat: " + location.getLatitude() +
                                "Long:" + location.getLongitude());
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
