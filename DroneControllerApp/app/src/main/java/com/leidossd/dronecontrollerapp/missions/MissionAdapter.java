package com.leidossd.dronecontrollerapp.missions;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.leidossd.dronecontrollerapp.R;

/**
 * Mission Adapter Class:
 * Contains the logic for putting items in the saved missions list, by defining
 * view holders, which are the containers for each item in the list, defining the text for each item in
 * the list, and handling the logic when an item on the list is clicked on.
 * <p>
 * Manages a separate MissionSaver than handles the saving and loading of all missions.
 * <p>
 * Any activity that uses it must implement its corresponding listener, so it can send missions through it.
 */
public class MissionAdapter extends RecyclerView.Adapter<MissionAdapter.MissionHolder> {
    private MissionAdapterListener missionAdapterListener;
    private MissionSaver missionSaver;

    public MissionAdapter(Context context) {
        //Any activity that uses this adapter MUST implement this, so the adapter can send it
        //data through send mission.
        if (context instanceof MissionAdapterListener) {
            this.missionAdapterListener = (MissionAdapterListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement MissionAdapterListener");
        }
        //Create MissionSaver and loads it with missions.
        this.missionSaver = new MissionSaver((Activity) context);
        missionSaver.unloadMissions();
    }

    //Creates the viewholders, which contain the UI elements to be displayed by the MissionAdapter.
    @NonNull
    @Override
    public MissionHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.saved_mission_rows, viewGroup, false);
        MissionHolder vh = new MissionHolder(v);
        //This is what gets executed when the UI element that corresponds to this one gets clicked on.
        v.setOnClickListener((View view) -> {
            int pos = vh.getAdapterPosition();
            sendMission(pos);
        });
        return vh;
    }

    //Binds the viewholder to the adapter, and sets the properties of the viewholder.
    @Override
    public void onBindViewHolder(@NonNull MissionHolder viewHolder, int pos) {
        viewHolder.mTitle.setText(missionSaver.getMissionAt(pos).getTitle());
        viewHolder.description.setText(missionSaver.getMissionAt(pos).getDescription());
        viewHolder.argsString.setText(missionSaver.getMissionAt(pos).argsToString());
        viewHolder.deleteBtn.setOnClickListener(v -> {
            deleteMission(missionSaver.getMissionAt(pos), pos);
        });
    }

    @Override
    public int getItemCount() {
        return missionSaver.size();
    }

    //Adds new mission to the MissionAdapter, and sends it to MissionSaver to save it.
    public void addMission(Mission mission) {
        missionSaver.saveMission(mission);
        notifyItemInserted(getItemCount() + 1);
    }

    public void deleteMission(Mission mission, int pos) {
        missionSaver.deleteMission(mission);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, missionSaver.size());
    }

    //Gets mission from missionSaver and sends it to the associated Activity.
    //The activity MUST implement missionClicked.
    private void sendMission(int pos) {
        Mission mission = missionSaver.getMissionAt(pos);
        missionAdapterListener.missionClicked(mission);
    }

    //Interface used to interact with the associated activity.
    public interface MissionAdapterListener {
        void missionClicked(Mission mission);
    }

    //Defines custom UI list container, with properties specific to missions.
    class MissionHolder extends RecyclerView.ViewHolder {
        TextView mTitle;
        TextView description;
        TextView argsString;
        Button deleteBtn;

        MissionHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.title);
            argsString = itemView.findViewById(R.id.args_string);
            description = itemView.findViewById(R.id.description);
            deleteBtn = itemView.findViewById(R.id.btn_delete);
        }
    }
}
