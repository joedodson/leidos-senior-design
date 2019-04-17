package com.leidossd.dronecontrollerapp.missions.ui.fragments;

import android.Manifest;
import android.animation.LayoutTransition;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
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
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.leidossd.djiwrapper.Coordinate;
import com.leidossd.dronecontrollerapp.R;
import com.leidossd.dronecontrollerapp.missions.SurveillanceMission;
import com.leidossd.dronecontrollerapp.missions.ui.MissionCreateListener;
import com.leidossd.dronecontrollerapp.missions.ui.MissionMenuAdapter;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Locale;

import static com.leidossd.dronecontrollerapp.MainApplication.showToast;

public class SurveillanceFragment extends Fragment implements OnMapReadyCallback {
    private static final float MIN_ZOOM = 17.0f;
    private static final float MAX_ZOOM = 21.0f;

    private View.OnClickListener createButtonListener;
    private MissionCreateListener surveillanceFragmentListener;

    private EditText missionName;
    private CheckBox saveCheckbox;
    private TextView positionText;
    private Button mapScaleButton;
    private GoogleMap googleMap;
    private MapView mapView;
    private FusedLocationProviderClient fusedLocationClient;
    private EditText angleText;
    private static Marker currentMarker;

    private Location location;
    private Coordinate destination = null;
    private int locationPermission;

    private final static float DISTANCE_SCALE = 1.0f;

    View.OnClickListener enlargeListener;
    View.OnClickListener collapseListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createButtonListener = new View.OnClickListener() {
            public void onClick(View view) {
                if (destination != null) {
                    float cameraAngle = Float.parseFloat(angleText.getText().toString());
                    if(cameraAngle >= -90 && cameraAngle <= 30) {
                        SurveillanceMission sm = new SurveillanceMission("New Mission", destination, cameraAngle);
                        surveillanceFragmentListener.createMission(sm, saveCheckbox.isChecked());
                    } else {
                        showToast("Angle must be between -90 and 30 degrees.");
                    }
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

        Button createButton = view.findViewById(R.id.button_create);
        missionName = view.findViewById(R.id.mission_name);
        saveCheckbox = view.findViewById(R.id.mission_save);
        positionText = view.findViewById(R.id.text_position);
        createButton.setOnClickListener(createButtonListener);
        angleText = view.findViewById(R.id.angle_box);

        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        mapScaleButton = view.findViewById(R.id.btn_map_scale);

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
            if (currentMarker != null) {
                currentMarker.remove();
            }
            currentMarker = googleMap.addMarker(new MarkerOptions().position(point));

            Location pLocation = new Location("");
            pLocation.setLatitude(point.latitude);
            pLocation.setLongitude(point.longitude);

            destination = getDistance(location, pLocation);

            positionText.setText(String.format(Locale.getDefault(), "X: %f, Y: %f, Z: %f", destination.getX(), destination.getY(), destination.getZ()));
        });

        updateMap();
        getLocation();
        mapView.setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.tv_map_loading).setVisibility(View.INVISIBLE);
    }

    private void disableControls(GoogleMap gmap){
        googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setZoomGesturesEnabled(false);
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    private Coordinate getDistance(Location l1, Location l2) {
        float distance = l1.distanceTo(l2) * DISTANCE_SCALE;
        double angleRadians = Math.toRadians(l1.bearingTo(l2));

        float x = (float) (distance * Math.cos(angleRadians));
        float y = (float) (distance * Math.sin(angleRadians));

        return new Coordinate(x, y, 0);
    }

    private Coordinate getTestDistance(Location l1, Location l2, int mult) {
        Point p = googleMap.getProjection().toScreenLocation(new LatLng(l1.getLatitude(), l1.getLongitude()));
        Point q = googleMap.getProjection().toScreenLocation(new LatLng(l2.getLatitude(), l2.getLongitude()));
        float x = (float) (q.x - p.x) / p.x;
        float y = (float) -(q.y - p.y) / p.y;

        return new Coordinate(x, y, 0);
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

    private void updateMap() {
        if (googleMap == null) {
            return;
        }
        try {
            if (locationPermission == PackageManager.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(true);
            } else {
                googleMap.setMyLocationEnabled(false);
                location = null;
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
