package com.leidossd.dronecontrollerapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.leidossd.dronecontrollerapp.missions.Mission;
import com.leidossd.dronecontrollerapp.missions.MissionRunner;
import com.leidossd.utils.MissionAction;

import java.util.ArrayList;

public class MissionActivity extends AppCompatActivity {
    //Constants for inner classes
    private static final int DEFAULT_VH = 0;
    private static final int MISSION_VH = 1;
    private static final int CONFIRM_MISSION = 1001;
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

    private FloatingActionButton createMissionButton;

    private RecyclerView listView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private ArrayList<MissionFrame> savedMissions;
    private MissionRunner missionRunner;
    private Mission currentMission = null;

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

        savedMissions = new ArrayList<>();
        for(int i = 0; i < 30; i++){
            String command;
            //TODO: DEBUG CODE -- FIX WHEN DONE
            if(i == 0) {
                command = "Go West and do surveillance.";
            } else if (i == 1){
                command = "Go North and do surveillance.";
            } else if (i == 2){
                command = "Go South-East and do surveillance.";
            } else {
                command = "Does Nothing.";
            }
            savedMissions.add(new MissionFrame("Default Mission " + i, MissionAction.WAYPOINT_MISSION,true, command));
        }
        adapter = new MissionAdapter(savedMissions);
        missionRunner = new MissionRunner(this);
        listView.setAdapter(adapter);
    }

    public void onClicked(View view) {
        PopupMenu popupMenu = new PopupMenu(MissionActivity.this, createMissionButton);
        popupMenu.getMenuInflater().inflate(R.menu.select_mission, popupMenu.getMenu());

        //TODO: Clean-up Toast call.
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            MissionAction action = null;

            switch(menuItem.getItemId()){
                case (R.id.mtype_waypoint):
                    action = MissionAction.WAYPOINT_MISSION;
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
                intent.putExtra("MissionType", action);
                startActivity(intent);
            }
            return true;
        });

        popupMenu.show();
    }

    public void updateStatus(){
        if(currentMission != null) {
            if (currentMission.getStatus().equals("READY")) {
                missionRunner.startMission(this, currentMission);
                noMissionText.setVisibility(View.INVISIBLE);
                missionText.setVisibility(View.VISIBLE);
                droneImage.setVisibility(View.VISIBLE);
                nextArrow.setVisibility(View.VISIBLE);
                missionBar.setClickable(true);
            } else if (currentMission.getStatus().equals("COMPLETED") ||
                    currentMission.getStatus().equals("FAILED")){
                currentMission = null;
                noMissionText.setVisibility(View.VISIBLE);
                missionText.setVisibility(View.INVISIBLE);
                droneImage.setVisibility(View.INVISIBLE);
                nextArrow.setVisibility(View.INVISIBLE);
                missionBar.setClickable(false);
            }
        } else {
            throw new RuntimeException("No mission to start");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == AppCompatActivity.RESULT_CANCELED) {
            Toast.makeText(MissionActivity.this, "Cancelled Request.", Toast.LENGTH_SHORT).show();
        }
        else if (resultCode == AppCompatActivity.RESULT_OK) {
//            Bundle resMission = data.getExtras();
            String resMission = "Filler";
            if(resMission != null){
                if(currentMission == null) {
                    currentMission = data.getParcelableExtra("Mission");
                } else {
                    Toast.makeText(MissionActivity.this, "Mission already Started.", Toast.LENGTH_SHORT).show();
                }
                if (currentMission != null) {
                    Toast.makeText(MissionActivity.this, "Mission Started.", Toast.LENGTH_SHORT).show();
                    updateStatus();
                } else {
                    throw new RuntimeException("Attempted to create mission; received invalid mission.");
                }
            } else {
                throw new RuntimeException("Attempted to create mission, but no mission received.");
            }
        }
    }

    public void checkMission(View view) {
        Toast.makeText(MissionActivity.this, "Mission Status.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Mission Adapter Class.  Contains the logic for putting items in the saved missions list, by defining
     * view holders, which are the containers for each item in the list, defining the text for each item in
     * the list, and handling the logic when an item on the list is clicked on.
     */
    private class MissionAdapter extends RecyclerView.Adapter<MissionAdapter.ViewHolder> {
        private ArrayList<MissionFrame> savedMissions;

        MissionAdapter(ArrayList<MissionFrame> savedMissions) {
            this.savedMissions = savedMissions;
        }

        @NonNull
        @Override
        public MissionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.saved_mission_rows, viewGroup, false);
            ViewHolder vh;
            if(i == DEFAULT_VH) {  //Hard-coded missions
                vh = new DefaultHolder(v);
                v.setOnClickListener(view -> {
                   // Toast.makeText(MissionActivity.this, "This is a default holder.", Toast.LENGTH_SHORT).show();
                    AppCompatActivity a = (AppCompatActivity)v.getContext();
                    Intent intent = new Intent(a, ConfirmMissionActivity.class);
                    a.startActivityForResult(intent, CONFIRM_MISSION);
                });
            } else if (i == MISSION_VH) {  //Saved missions
                vh = new MissionHolder(v);
                v.setOnClickListener(view -> {
                    TextView text = view.findViewById(R.id.title);
                    Toast.makeText(MissionActivity.this, "" + text.getText().toString(), Toast.LENGTH_SHORT).show();
                });
            } else {
                throw new RuntimeException("Undefined view holder type for RecyclerView.");
            }
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull MissionAdapter.ViewHolder viewHolder, int i) {
            viewHolder.mTitle.setText(savedMissions.get(i).getMissionName());
            viewHolder.mType.setText(savedMissions.get(i).getMissionTypeString());
            viewHolder.description.setText(savedMissions.get(i).getMissionDescription());
        }

        @Override
        public int getItemCount() {
            return savedMissions.size();
        }

        @Override
        public int getItemViewType(int position){
            if (position < 3) return 0;
            return 1;
        }

        public abstract class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mTitle;
            public TextView mType;
            public TextView description;
            public int missionType;

            public ViewHolder(@NonNull View itemView){
                super(itemView);
                mTitle = itemView.findViewById(R.id.title);
                mType = itemView.findViewById(R.id.mtype);
                description = itemView.findViewById(R.id.description);
            }
        }

        public class MissionHolder extends MissionAdapter.ViewHolder {
            public MissionHolder(@NonNull View itemView) {
                super(itemView);
                missionType = 1;
            }
        }

        public class DefaultHolder extends MissionAdapter.ViewHolder {
            public int id;
            public DefaultHolder(@NonNull View itemView) {
                super(itemView);
                missionType = 0;
            }
        }
    }

    /**
     * Used to define the barriers between each item on the saved missions list.
     */
    private class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private Drawable divider;

        public DividerItemDecoration(Context context) {
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

    private class MissionFrame {
        private final String missionName;
        private final MissionAction missionType;
        private final boolean isDefault;
        private final String missionDescription;

        public MissionFrame(String missionName, MissionAction missionType, boolean isDefault, String missionDescription){
            this.missionName = missionName;
            this.missionType = missionType;
            this.isDefault = isDefault;
            this.missionDescription = missionDescription;
        }

        public String getMissionName() {
            return missionName;
        }

        public MissionAction getMissionType() {
            return missionType;
        }

        public String getMissionTypeString() {
            if(missionType == MissionAction.WAYPOINT_MISSION) return "Waypoint Mission.";
            return "This mission does not have a type.";
        }

        public boolean getIsDefault() {
            return isDefault;
        }

        public String getMissionDescription() {
            return missionDescription;
        }
    }
}
