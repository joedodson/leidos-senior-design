package com.leidossd.dronecontrollerapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.leidossd.utils.MissionAction;

import java.util.ArrayList;

public class MissionActivity extends AppCompatActivity {
    //Constants for inner classes
    private static final int DEFAULT_VH = 0;
    private static final int MISSION_VH = 1;
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

    private FloatingActionButton createMissionButton;

    private MissionAction action;

    private RecyclerView listView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private ArrayList<String> savedMissions;
    private ArrayList<String> missionType;
    private ArrayList<String> command;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);

        createMissionButton = findViewById(R.id.create_mission);
        listView = findViewById(R.id.saved_missions);
        listView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(layoutManager);

        listView.addItemDecoration(new DividerItemDecoration(this));

        savedMissions = new ArrayList<>();
        missionType = new ArrayList<>();
        command = new ArrayList<>();
        for(int i = 0; i < 30; i++){
            savedMissions.add("Default Mission " + i);
            missionType.add("Waypoint Mission");
            //TODO: DEBUG CODE -- FIX WHEN DONE
            if(i == 0) {
                command.add("Go West and do surveillance.");
            } else if (i == 1){
                command.add("Go North and do surveillance.");
            } else if (i == 2){
                command.add("Go South-East and do surveillance.");
            } else {
                command.add("Does Nothing.");
            }
        }
        adapter = new MissionAdapter(savedMissions, missionType, command);
        listView.setAdapter(adapter);
    }

    public void onClicked(View view) {
        PopupMenu popupMenu = new PopupMenu(MissionActivity.this, createMissionButton);
        popupMenu.getMenuInflater().inflate(R.menu.select_mission, popupMenu.getMenu());

        //TODO: Clean-up Toast call.
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            switch(menuItem.getItemId()){
                case (R.id.mtype_waypoint):
                    action = MissionAction.WAYPOINT_MISSION;
                    break;
                //TODO: Define other mission types.
                case (R.id.mtype_default):
                case (R.id.mtype_custom):
                    Toast.makeText(MissionActivity.this, "" + menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    action = null;
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

    private class MissionAdapter extends RecyclerView.Adapter<MissionAdapter.ViewHolder> {
        private ArrayList<String> savedMissions;
        private ArrayList<String> missionType;
        private ArrayList<String> command;

        MissionAdapter(ArrayList<String> savedMissions, ArrayList<String> missionType, ArrayList<String>command) {
            this.savedMissions = savedMissions;
            this.missionType = missionType;
            this.command = command;
        }

        @NonNull
        @Override
        public MissionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.saved_mission_rows, viewGroup, false);
            ViewHolder vh;
            if(i == DEFAULT_VH) {
                vh = new DefaultHolder(v);
                v.setOnClickListener(view -> {
                   // Toast.makeText(MissionActivity.this, "This is a default holder.", Toast.LENGTH_SHORT).show();
                    Context c = v.getContext();
                    Intent intent = new Intent(c, ConfirmMissionActivity.class);
                    c.startActivity(intent);
                });
            } else if (i == MISSION_VH) {
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
            viewHolder.mTitle.setText(savedMissions.get(i));
            viewHolder.mType.setText(missionType.get(i));
            viewHolder.description.setText(command.get(i));
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

    private class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private Drawable divider;

        /**
         * Default divider will be used
         */
        public DividerItemDecoration(Context context) {
            final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
            divider = styledAttributes.getDrawable(0);
            styledAttributes.recycle();
        }

        /**
         * Custom divider will be used
         */
        public DividerItemDecoration(Context context, int resId) {
            divider = ContextCompat.getDrawable(context, resId);
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
