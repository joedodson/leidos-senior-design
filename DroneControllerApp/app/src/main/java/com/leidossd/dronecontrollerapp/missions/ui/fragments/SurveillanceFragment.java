package com.leidossd.dronecontrollerapp.missions.ui.fragments;

import android.Manifest;
import android.animation.LayoutTransition;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.SphericalUtil;
import com.leidossd.djiwrapper.Coordinate;
import com.leidossd.djiwrapper.FlightControllerWrapper;
import com.leidossd.dronecontrollerapp.MainApplication;
import com.leidossd.dronecontrollerapp.R;
import com.leidossd.dronecontrollerapp.missions.SurveillanceMission;
import com.leidossd.dronecontrollerapp.missions.ui.MissionCreateListener;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKManager;

import static com.leidossd.dronecontrollerapp.MainApplication.showToast;

public class SurveillanceFragment extends Fragment implements OnMapReadyCallback {
    private static final float MIN_ZOOM = 17.0f;
    private static final float MAX_ZOOM = 21.0f;

    private View.OnClickListener createButtonListener;
    private MissionCreateListener surveillanceFragmentListener;

    private CheckBox saveCheckbox;
    private TextView positionText;
    private Button mapScaleButton;
    private GoogleMap googleMap;
    private MapView mapView;
    private FusedLocationProviderClient fusedLocationClient;
    private static Marker waypointMarker;
    private static Marker droneLocationMarker;
    private static FlightControllerWrapper flightController;

    private Location location;
    private Coordinate destination = null;
    private int locationPermission;

    private final static float DISTANCE_SCALE = 0.2f;

    View.OnClickListener enlargeListener;
    View.OnClickListener collapseListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createButtonListener = view -> {
            if (destination != null) {
                SurveillanceMission sm = new SurveillanceMission(destination);
                surveillanceFragmentListener.createMission(sm, saveCheckbox.isChecked());
            } else {
                showToast("Please Enter a Location.");
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_surveillance, container, false);

        Button createButton = view.findViewById(R.id.button_create);
        saveCheckbox = view.findViewById(R.id.mission_save);
        positionText = view.findViewById(R.id.text_position);
        createButton.setOnClickListener(createButtonListener);

        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        mapScaleButton = view.findViewById(R.id.btn_map_scale);

        // TODO make this call flightcontrollerwrapper's getHomeLocation function when implemented
        view.findViewById(R.id.btn_map_center).setOnClickListener(null);

        saveCheckbox = view.findViewById(R.id.mission_save);

        ViewGroup mapContainer = view.findViewById(R.id.map_container);
        ViewGroup.LayoutParams mapContainerParams = mapContainer.getLayoutParams();

        // enable transitions when map enlarges
        mapContainer.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        mapView.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        ViewGroup missionInfoContainer = view.findViewById(R.id.mission_info_container);
        missionInfoContainer.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        collapseListener = v -> {
            mapContainerParams.width = 0;
            mapContainer.setLayoutParams(mapContainerParams);
            mapScaleButton.setOnClickListener(enlargeListener);
            mapScaleButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_expand_gray, null));
        };

        enlargeListener = v -> {
            mapContainerParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            mapContainer.setLayoutParams(mapContainerParams);
            mapScaleButton.setOnClickListener(collapseListener);
            mapScaleButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_collapse_gray, null));
        };

        mapScaleButton.setOnClickListener(enlargeListener);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);

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

        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        googleMap.setPadding(0, 75, 0, 0);

        googleMap.setMinZoomPreference(MIN_ZOOM);
        googleMap.setMaxZoomPreference(MAX_ZOOM);

//        disableControls(gMap);

        TileProvider tileProvider = new CoordTileProvider();

        googleMap.addTileOverlay(new TileOverlayOptions()
                .tileProvider(tileProvider));

        googleMap.setOnMapClickListener(point -> {
            if (waypointMarker != null) {
                waypointMarker.remove();
            }
            waypointMarker = googleMap.addMarker(new MarkerOptions().position(point));

            Location pLocation = new Location("");
            pLocation.setLatitude(point.latitude);
            pLocation.setLongitude(point.longitude);

            destination = getDistance(location, pLocation);

            positionText.setText(String.format(Locale.getDefault(), "X: %f, Y: %f, Z: %f", destination.getX(), destination.getY(), destination.getZ()));
        });

        getLocation();
        mapView.setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.tv_map_loading).setVisibility(View.INVISIBLE);
    }

    private Coordinate getDistance(Location l1, Location l2) {
        float distance = l1.distanceTo(l2) * DISTANCE_SCALE;
        double angleRadians = Math.toRadians(l1.bearingTo(l2));

        float y = (float) (distance * Math.cos(angleRadians));
        float x = (float) (distance * Math.sin(angleRadians));

        return new Coordinate(x, y, 0);
    }

    private LatLng coordToLatLng(Coordinate coord) {
        double dist = Math.sqrt(Math.pow(coord.getX(),2) + Math.pow(coord.getY(), 2));

        if(dist == 0) {
            return flightController.getHome();
        } else {
            return SphericalUtil.computeOffset(flightController.getHome(), dist, coord.angleFacing());
        }
    }

    private void getLocation() {
        try {
            if (getActivity() != null && locationPermission == PackageManager.PERMISSION_GRANTED) {
                Task locationResult = fusedLocationClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        location = (Location) task.getResult();

                        if (location != null) {
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(location.getLatitude(),
                                            location.getLongitude()), MAX_ZOOM));

                            LatLng pos;
                            flightController = FlightControllerWrapper.getInstance();
                            if(flightController != null) {
                                if(!flightController.isHomeSet()) {
                                    pos = new LatLng(location.getLatitude(), location.getLongitude());
                                    flightController.setHome(pos);
                                } else {
                                    pos = coordToLatLng(flightController.getPosition());
                                }

                                flightController.syncDirection();
                                droneLocationMarker = googleMap.addMarker(new MarkerOptions()
                                        .position(pos)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_drone_white_direction))
                                        .anchor(0.5f, 0.5f)
                                        .rotation(flightController.getAngleFacing()));

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        flightController.syncDirection();
                                        droneLocationMarker.setRotation(flightController.getAngleFacing());
                                        handler.postDelayed(this, 100);
                                    }
                                }, 100);
                            }
                        }
                    } else {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0, 0), MAX_ZOOM));
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onResume() {
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

    // loosely based on sample code https://github.com/googlemaps/android-samples/blob/master/ApiDemos/java/app/src/main/java/com/example/mapdemo/TileCoordinateDemoActivity.java
    private static class CoordTileProvider implements TileProvider {

        private static final int MAX_TILE_SIZE = 256;

        private final Bitmap mBorderTile;
        Paint borderPaint;

        CoordTileProvider() {
            borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setColor(Color.RED);
            borderPaint.setStrokeWidth(0);
            mBorderTile = Bitmap.createBitmap(MAX_TILE_SIZE,
                    MAX_TILE_SIZE, android.graphics.Bitmap.Config.ARGB_8888);
        }

        @Override
        public Tile getTile(int x, int y, int zoom) {
            Bitmap coordTile = getGridTileBitmap(zoom);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            coordTile.compress(Bitmap.CompressFormat.PNG, 0, stream);
            byte[] bitmapData = stream.toByteArray();

            Tile tile = new Tile(MAX_TILE_SIZE,
                    MAX_TILE_SIZE, bitmapData);
            Log.d("tile dimens", String.format("width: %s, height: %s", tile.width, tile.height));

            return new Tile(MAX_TILE_SIZE,
                    MAX_TILE_SIZE, bitmapData);
        }

        private Bitmap getGridTileBitmap(int zoom) {
            Bitmap copy;
            synchronized (mBorderTile) {
                copy = mBorderTile.copy(android.graphics.Bitmap.Config.ARGB_8888, true);
            }
            Canvas canvas = new Canvas(copy);

            int num_tiles = MAX_TILE_SIZE / ((int) Math.pow(2, (zoom - MIN_ZOOM) + 3));
            int tile_size = MAX_TILE_SIZE / num_tiles;
            for (int row = 0; row < num_tiles + 1; ++row) {
                canvas.drawLine(0, row * tile_size, MAX_TILE_SIZE, row * tile_size, borderPaint);
                canvas.drawLine(row * tile_size, 0, row * tile_size, MAX_TILE_SIZE, borderPaint);
            }
            return copy;
        }
    }
}
