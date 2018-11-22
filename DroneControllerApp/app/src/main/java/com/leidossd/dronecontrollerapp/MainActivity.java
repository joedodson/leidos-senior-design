package com.leidossd.dronecontrollerapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
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
import utils.MenuAction;

public class MainActivity extends AppCompatActivity implements MenuFragment.fragmentInteractionListener {
    private static final String TAG = MainActivity.class.getName();
    MainApplication app = (MainApplication) getApplication();

    // receiver to wait for 'MainApplication' to notify connection status change
    private BroadcastReceiver connectionChangeReceiver;

    Toolbar actionBar;

    FragmentManager fragmentManager;
    MenuFragment menuFragment;

    DJISDKManager.SDKManagerCallback DJISDKManagerCallback;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectionChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateStatus(getConnectionStatusStringId());
            }
        };

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
    private void updateStatus(int stringId) {
        MenuView.ItemView status = findViewById(R.id.action_bar_status);
        status.setTitle(getString(stringId));
        showToast(getString(stringId));
        Log.d(TAG, String.format("Update UI: %s", getString(stringId)));
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