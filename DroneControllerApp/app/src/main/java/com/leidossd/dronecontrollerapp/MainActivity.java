package com.leidossd.dronecontrollerapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import dji.sdk.base.BaseProduct;
import dji.sdk.sdkmanager.DJISDKManager;
import utils.DroneConnectionStatus;
import utils.MenuAction;

import static utils.IntentAction.*;
import static utils.DroneConnectionStatus.*;

public class MainActivity extends AppCompatActivity implements MenuFragment.fragmentInteractionListener {
    private static final String TAG = MainActivity.class.getName();
    MainApplication app = (MainApplication) getApplication();

    // receiver to wait for 'MainApplication' to notify connection status change
    private BroadcastReceiver connectionChangeReceiver;

    Toolbar actionBar;

    FragmentManager fragmentManager;
    MenuFragment menuFragment;

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

        // add the menu fragment, but don't show it
        fragmentManager.beginTransaction()
                .add(R.id.menu_fragment_container, menuFragment)
                .hide(menuFragment)
                .commit();

        actionBar = findViewById(R.id.status_actionbar);

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
            case R.id.action_bar_menu:
                actionBar.animate()
                        .translationY(-200)
                        .setDuration(getResources().getInteger(R.integer.animation_duration_ms_short))
                        .start();

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
                showToast("Developer");
                break;
            case OPEN_SETTINGS:
                showToast("Settings");
                break;
            default:
                showToast("Closing Menu");
                actionBar.animate()
                        .translationY(0)
                        .setDuration(getResources().getInteger(R.integer.animation_duration_ms_short))
                        .start();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.animator.show_menu, R.animator.hide_menu)
                        .hide(menuFragment)
                        .commit();
        }
    }

    int getConnectionStatusStringId() {
        BaseProduct drone = DJISDKManager.getInstance().getProduct();
        return (drone != null && drone.isConnected()) ?
                R.string.activity_droneconnection_statusConnected :
                R.string.activity_droneconnection_statusDisconnected;
    }

    // remove callback to prevent failed message, set correct status, and log result
    private void updateDroneStatus(String droneStatus) {
        MenuView.ItemView statusView = findViewById(R.id.action_bar_status);

        if(droneStatus.equals(DRONE_CONNECTED.getStatus()) || droneStatus.equals(DRONE_DISCONNECTED.getStatus())) {
            statusView.setTitle(droneStatus + DJISDKManager.getInstance().getProduct().getModel());
        } else {
            statusView.setTitle(DRONE_CONNECTION_ERROR.getStatus());
        }
        Log.d(TAG, String.format("Updated drone status with: %s", droneStatus));
    }

    private void configureActionBar() {
        actionBar.setLogo(R.drawable.ic_leidos);
        actionBar.setTitle("LSD");
        setSupportActionBar(actionBar);
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}