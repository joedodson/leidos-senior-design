package com.leidossd.dronecontrollerapp;

import android.os.Bundle;
import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MissionActivity extends AppCompatActivity {

    private FloatingActionButton createMissionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);

        createMissionButton = findViewById(R.id.create_mission);
    }

    public void onClicked(View view) {
        PopupMenu popupMenu = new PopupMenu(MissionActivity.this, createMissionButton);
        popupMenu.getMenuInflater().inflate(R.menu.select_mission, popupMenu.getMenu());

        //TODO: Clean-up Toast call
        popupMenu.setOnMenuItemClickListener(menuItem -> {
           Toast.makeText(MissionActivity.this, "" + menuItem.getTitle(), Toast.LENGTH_SHORT).show();
           return true;
        });

        popupMenu.show();
    }
}
