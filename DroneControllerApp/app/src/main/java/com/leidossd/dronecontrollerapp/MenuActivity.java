package com.leidossd.dronecontrollerapp;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.leidossd.dronecontrollerapp.compass.CompassCalibrationActivity;
import com.leidossd.dronecontrollerapp.missions.ui.MissionSelectionActivity;
import com.leidossd.dronecontrollerapp.missions.ui.MissionServiceExampleActivity;
import com.leidossd.utils.MenuAction;
import com.leidossd.utils.SwipeGestureListener;

import dji.sdk.sdkmanager.DJISDKManager;

import static com.leidossd.dronecontrollerapp.MainApplication.getDroneInstance;
import static com.leidossd.dronecontrollerapp.MainApplication.showToast;
import static com.leidossd.utils.DroneConnectionStatus.DRONE_CONNECTED;
import static com.leidossd.utils.IntentAction.CONNECTION_CHANGE;

public class MenuActivity extends AppCompatActivity implements
        MenuFragment.fragmentInteractionListener {

    Toolbar actionBar;
    private GestureDetectorCompat gestureDetector;

    FragmentManager fragmentManager;
    MenuFragment menuFragment;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new Handler();
        fragmentManager = getSupportFragmentManager();
        menuFragment = new MenuFragment();

        // add the menu fragment, but don't show it
        fragmentManager.beginTransaction()
                .add(R.id.menu_fragment_container, menuFragment)
                .hide(menuFragment)
                .commit();

        // detects touch gestures to show/hide menu and actionbar
        gestureDetector = new GestureDetectorCompat(this,
                new SwipeGestureListener(new SwipeGestureListener.SwipeListener() {
                    @Override
                    public void onLeftSwipe() {
                        showMenu();
                        hideActionBar();
                    }

                    @Override
                    public void onRightSwipe() {
                        hideMenu();
                        showActionBar();
                    }

                    @Override
                    public void onUpSwipe() {
                        hideActionBar();
                    }

                    @Override
                    public void onDownSwipe() {
                        showActionBar();
                    }
                }));

        // MainApplication sends local broadcast when connection status changes
        // receiver to wait for 'MainApplication' to notify connection status change
        BroadcastReceiver connectionChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent connectionChangeIntent) {
                String droneStatus = connectionChangeIntent.getStringExtra(CONNECTION_CHANGE.getResultKey());
                updateDroneStatus(droneStatus);
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(connectionChangeReceiver, new IntentFilter(CONNECTION_CHANGE.getActionString()));
    }

    @Override
    public void setContentView(int layoutResID) {
        @SuppressLint("InflateParams")
        FrameLayout fullView = (FrameLayout) getLayoutInflater().inflate(R.layout.activity_menu, null);
        FrameLayout childView = fullView.findViewById(R.id.child_activity_container);
        actionBar = fullView.findViewById(R.id.status_actionbar);

        configureActionBar();
        getLayoutInflater().inflate(layoutResID, childView);
        super.setContentView(fullView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_actionbar, menu);

        Handler handler = new Handler();

        handler.postDelayed(() -> {
            if (MainApplication.getDroneInstance() != null) {
                updateDroneStatus(DRONE_CONNECTED.toString());
            }
        }, 500);
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
            case R.id.action_bar_compass:
                if (MainApplication.getDroneInstance() == null || !MainApplication.getDroneInstance().isConnected())
                    //droneNotConnectedDialog.show();
                    MainApplication.showToast("showing drone not connected dialog");
                else
                    startActivity(new Intent(this, CompassCalibrationActivity.class));
                break;
            case R.id.action_bar_battery:
                showToast("Battery");
                break;
            case R.id.action_bar_main_menu:
                hideActionBar();
                showMenu();
                break;
            default:
                return false;
        }
        return true;
    }

    // handles actions clicked in side menu
    public void onMenuSelect(MenuAction action) {
        switch (action) {
            case OPEN_HOME:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case OPEN_MISSIONS:
                startActivity(new Intent(this, MissionSelectionActivity.class));
                break;
            case OPEN_DEVELOPER:
                // Start Brians Activity
                startActivity(new Intent(this, MissionServiceExampleActivity.class));
                break;
            case OPEN_SETTINGS:
//                showToast("Settings");
//                break;
            case OPEN_COMPASS:
                if (MainApplication.getDroneInstance() == null || !MainApplication.getDroneInstance().isConnected())
                    //droneNotConnectedDialog.show();
                    MainApplication.showToast("showing drone not connected dialog");
                else {
                    startActivity(new Intent(this, CompassCalibrationActivity.class));
                }
                break;
            case OPEN_GRID_VIEW:
//                startActivity(new Intent(this, GridParentActivity.class));
//                break;
            default:
                showActionBar();
        }
        showActionBar();
        hideMenu();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void configureActionBar() {
        actionBar.setLogo(R.drawable.ic_leidos);
        actionBar.setTitle(R.string.app_name);
        setSupportActionBar(actionBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void showMenu() {
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.animator.show_menu, R.animator.hide_menu)
                .show(menuFragment)
                .commit();
    }

    private void hideMenu() {
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.animator.show_menu, R.animator.hide_menu)
                .hide(menuFragment)
                .commit();
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

    // remove callback to prevent failed message, set correct status, and log result
    private void updateDroneStatus(String droneStatus) {
        MenuView.ItemView statusView = findViewById(R.id.action_bar_status);
        String status = droneStatus;

        if (droneStatus.equals(DRONE_CONNECTED.toString())) {
            status += " to " + getDroneInstance().getModel().getDisplayName();

            handler.removeCallbacksAndMessages(null);

            Runnable updateBatteryRunnable = new Runnable() {
                @Override
                public void run() {
                    MainApplication.getDroneInstance().getBattery().setStateCallback(batteryState -> {

                        int batteryPercent = batteryState.getChargeRemainingInPercent();
                        MenuView.ItemView batteryText = findViewById(R.id.action_bar_battery);

                        if (batteryPercent > 0) {
                            runOnUiThread(() -> batteryText.setTitle(String.valueOf(batteryPercent) + "%"));
                        } else {
                            runOnUiThread(() -> batteryText.setTitle("ERR"));
                        }
                    });
                    handler.postDelayed(this, 10000);
                }
            };

            handler.post(updateBatteryRunnable);
        }

        if (statusView != null) {
            statusView.setTitle(status);
        }
    }
}
