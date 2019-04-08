package com.leidossd.dronecontrollerapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.leidossd.dronecontrollerapp.missions.Mission;
import com.leidossd.dronecontrollerapp.missions.MissionAdapter;
import com.leidossd.dronecontrollerapp.missions.MissionRunner;
import com.leidossd.dronecontrollerapp.missions.MissionRunnerService;
import com.leidossd.dronecontrollerapp.missions.Task;
import com.leidossd.utils.MissionAction;

public class MissionActivity extends MenuActivity implements MissionAdapter.MissionAdapterListener, Task.StatusUpdateListener{
    //Constants for inner classes
    private static final int CREATE_MISSION = 1001;
    private static final int CONFIRM_MISSION = 1002;
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

    private FloatingActionButton createMissionButton;

    private RecyclerView listView;
    private RecyclerView.LayoutManager layoutManager;
    private MissionAdapter adapter;
    private MissionRunner missionRunner;

    private TextView noMissionText;
    private TextView missionText;
    private ImageView droneImage;
    private ImageView nextArrow;
    private ConstraintLayout missionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);

        noMissionText = findViewById(R.id.text_no_mission);
        missionText = findViewById(R.id.text_mission);
        droneImage = findViewById(R.id.drone_image);
        nextArrow = findViewById(R.id.next_arrow);
        missionBar = findViewById(R.id.mission_bar);

        noMissionText.setVisibility(View.VISIBLE);
        missionText.setVisibility(View.INVISIBLE);
        droneImage.setVisibility(View.INVISIBLE);
        nextArrow.setVisibility(View.INVISIBLE);
        missionBar.setClickable(false);

        createMissionButton = findViewById(R.id.create_mission);
        listView = findViewById(R.id.saved_missions);
        listView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(layoutManager);

        listView.addItemDecoration(new DividerItemDecoration(this));

        adapter = new MissionAdapter(this);
        missionRunner = new MissionRunner(this,this);
        listView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateStatus(Task.TaskState.NOT_READY);
    }

    public void onClicked(View view) {
        PopupMenu popupMenu = new PopupMenu(MissionActivity.this, createMissionButton);
        popupMenu.getMenuInflater().inflate(R.menu.select_mission, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            MissionAction action = null;

            switch(menuItem.getItemId()){
                case (R.id.mtype_waypoint):
                    action = MissionAction.WAYPOINT_MISSION;
                    break;
                case (R.id.mtype_surveillance):
                    action = MissionAction.SURVEILLANCE_MISSION;
                    break;
                //TODO: Define other mission types.
                case (R.id.mtype_custom):
                    Toast.makeText(MissionActivity.this, "" + menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            if(action != null){
                Intent intent = new Intent(this, CreateMissionActivity.class);
                intent.putExtra("Mission Type", action);
                startActivityForResult(intent, CREATE_MISSION);
            }
            return true;
        });

        popupMenu.show();
    }

    public void updateStatus(Task.TaskState state){
        MissionRunnerService missionRunnerService = MissionRunner.missionRunnerService;
        if(missionRunnerService != null) {
            Mission mission = missionRunnerService.getCurrentMission();
            if (mission != null) {
                if (state.toString().equals("RUNNING")) {
                    missionText.setText(String.format("%s - %s", mission.getTitle(), state.toString()));
                    noMissionText.setVisibility(View.INVISIBLE);
                    missionText.setVisibility(View.VISIBLE);
                    droneImage.setVisibility(View.VISIBLE);
                    nextArrow.setVisibility(View.VISIBLE);
                    missionBar.setClickable(true);
                } else if (state.toString().equals("COMPLETED") ||
                        state.toString().equals("FAILED")) {
                    noMissionText.setVisibility(View.VISIBLE);
                    missionText.setVisibility(View.INVISIBLE);
                    droneImage.setVisibility(View.INVISIBLE);
                    nextArrow.setVisibility(View.INVISIBLE);
                    missionBar.setClickable(false);
                }
            }
        }
    }

    @Override
    public void statusUpdate(Task.TaskState status, String message){
        updateStatus(status);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == AppCompatActivity.RESULT_OK) {
            Mission mission = data.getParcelableExtra("Mission");
            boolean saveMission = data.getBooleanExtra("Save Mission", false);
            if(saveMission) adapter.addMission(mission);
            if (mission != null){
                missionRunner.startMission(this, mission);
            }
        }
    }

    public void checkMission(View view) {
        Intent intent = new Intent(this, MissionStatusActivity.class);
        startActivity(intent);
    }

    @Override
    public void missionClicked(Mission mission) {
        Intent intent = new Intent(this, ConfirmMissionActivity.class);
        intent.putExtra("Mission", mission);
        startActivityForResult(intent, CONFIRM_MISSION);
    }

    /**
     * Used to define the barriers between each item on the saved missions list.
     */
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
