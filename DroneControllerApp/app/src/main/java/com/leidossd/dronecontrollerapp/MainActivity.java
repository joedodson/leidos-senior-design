package com.leidossd.dronecontrollerapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import dji.sdk.sdkmanager.DJISDKManager;
import utils.MenuAction;

import static utils.IntentAction.*;
import static utils.DroneConnectionStatus.*;
import static com.leidossd.dronecontrollerapp.MainApplication.getDroneInstance;

public class MainActivity extends AppCompatActivity implements
        MenuFragment.fragmentInteractionListener {

    private static final String TAG = MainActivity.class.getName();
    MainApplication app = (MainApplication) getApplication();

    // receiver to wait for 'MainApplication' to notify connection status change
    private BroadcastReceiver connectionChangeReceiver;

    Toolbar actionBar;
    private GestureDetectorCompat gestureDetector;

    FragmentManager fragmentManager;
    MenuFragment menuFragment;
    LiveVideoFragment liveVideoFragment;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // MainApplication sends local broadcast when connection status changes
        connectionChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent connectionChangeIntent) {
                String droneStatus = connectionChangeIntent.getStringExtra(CONNECTION_CHANGE.getResultKey());
                updateDroneStatus(droneStatus);
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(connectionChangeReceiver, new IntentFilter(CONNECTION_CHANGE.getActionString()));

        fragmentManager = getSupportFragmentManager();
        menuFragment = new MenuFragment();
        liveVideoFragment = new LiveVideoFragment();

        // add the menu fragment, but don't show it
        fragmentManager.beginTransaction()
                .add(R.id.menu_fragment_container, menuFragment)
                .hide(menuFragment)
                .commit();

        actionBar = findViewById(R.id.status_actionbar);
        gestureDetector = new GestureDetectorCompat(this, new GestureListener());

        // hides the status bar
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        configureActionBar();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_actionbar, menu);
        return true;
    }

    // Handles click events of items in action bar.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bar_status:
                showToast("Status");
                DJISDKManager.getInstance().startConnectionToProduct();
                break;
            case R.id.action_bar_gps:
                showToast("GPS");
                break;
            case R.id.action_bar_battery:
                showToast("Battery");
                break;
            case R.id.action_bar_main_menu:
                hideActionBar();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.animator.show_menu, R.animator.hide_menu)
                        .show(menuFragment)
                        .commit();
                break;
            default:
                return false;
        }
        return true;
    }

    // handles actions clicked in side menu
    public void onMenuSelect(MenuAction action) {
        switch (action) {
            case OPEN_MISSIONS:
                showToast("Missions");
                break;
            case OPEN_DEVELOPER:
                // Start Brians Activity
                startActivity(new Intent(this, SecondActivity.class));
                showToast("Developer");
                break;
            case OPEN_SETTINGS:
                showToast("Settings");
                break;
            default:
                showActionBar();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.animator.show_menu, R.animator.hide_menu)
                        .hide(menuFragment)
                        .commit();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {

            if(event1.getY() < event2.getY()) { // swipe down
                showActionBar();
            } else if(event1.getY() > event2.getY()) { // swipe up
                hideActionBar();
            }
            return true;
        }
    }


    // remove callback to prevent failed message, set correct status, and log result
    private void updateDroneStatus(String droneStatus) {
        MenuView.ItemView statusView = findViewById(R.id.action_bar_status);
        String status = droneStatus;

        if(droneStatus.equals(DRONE_CONNECTED.toString())) {
            status = status += " " + getDroneInstance().getModel().getDisplayName();
            startLiveVideo();
        } else if(droneStatus.equals(DRONE_DISCONNECTED.toString())) {
            stopLiveVideo();
        } else {
            status = DRONE_CONNECTION_ERROR.toString();
            stopLiveVideo();
        }

        statusView.setTitle(status);

        Log.d(TAG, String.format("Updated drone status with: %s", droneStatus));
    }

    private void configureActionBar() {
        actionBar.setLogo(R.drawable.ic_leidos);
        actionBar.setTitle("LSD");
        setSupportActionBar(actionBar);
    }

    private void hideActionBar() {
        actionBar.animate()
                .translationY(-200)
                .setDuration(getResources().getInteger(R.integer.animation_duration_ms_short))
                .start();
    }

    private void showActionBar() {
        actionBar.animate()
                .translationY(0)
                .setDuration(getResources().getInteger(R.integer.animation_duration_ms_short))
                .start();
    }

    private void startLiveVideo() {
        if(!liveVideoFragment.isAdded()) {
            fragmentManager.beginTransaction()
                    .add(R.id.live_video_fragment_container, liveVideoFragment)
                    .commitAllowingStateLoss();
        }
    }

    private void stopLiveVideo() {
        if(liveVideoFragment.isAdded()) {
            fragmentManager.beginTransaction()
                    .remove(liveVideoFragment)
                    .commitAllowingStateLoss();
        }
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}