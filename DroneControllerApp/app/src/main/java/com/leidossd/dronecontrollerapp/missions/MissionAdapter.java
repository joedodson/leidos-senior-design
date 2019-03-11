package com.leidossd.dronecontrollerapp.missions;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leidossd.dronecontrollerapp.R;

/**
 * Mission Adapter Class:
 * Contains the logic for putting items in the saved missions list, by defining
 * view holders, which are the containers for each item in the list, defining the text for each item in
 * the list, and handling the logic when an item on the list is clicked on.
 *
 * Manages a separate MissionSaver than handles the saving and loading of all missions in the form of a
 * MissionFrame.
 *
 * Any activity that uses it must implement its corresponding listener, so it can send missions through it.
 */
public class MissionAdapter extends RecyclerView.Adapter<MissionAdapter.MissionHolder> {
    private MissionAdapterListener missionAdapterListener;
    private MissionSaver missionSaver;

    public MissionAdapter(Context context) {
        if (context instanceof MissionAdapterListener) {
            this.missionAdapterListener = (MissionAdapterListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement MissionAdapterListener");
        }
        this.missionSaver = new MissionSaver((Activity)context, true);
        missionSaver.unloadMissions();
    }

    @NonNull
    @Override
    public MissionHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.saved_mission_rows, viewGroup, false);
        MissionHolder vh = new MissionHolder(v);
            v.setOnClickListener((View view) -> {
                int pos = vh.getAdapterPosition();
                sendMission(pos);
            });
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MissionHolder viewHolder, int pos) {
        viewHolder.mTitle.setText(missionSaver.getMissionFrameAt(pos).getMissionName());
        viewHolder.mType.setText(missionSaver.getMissionFrameAt(pos).getMissionType());
        viewHolder.description.setText(missionSaver.getMissionFrameAt(pos).getMissionDescription());
    }

    @Override
    public int getItemCount() {
        return missionSaver.size();
    }

    @Override
    public int getItemViewType(int pos){
        if (missionSaver.getMissionFrameAt(pos).getIsDefault()) return 0;
        return 1;
    }


    public void addMission(Mission mission){
        String des = "Drone flies.";
        MissionFrame missionFrame = new MissionFrame(mission.getTitle(), "Mission", false, des);
        missionSaver.saveMission(missionFrame);
        notifyItemInserted(getItemCount()+1);
    }

    private void sendMission(int pos){
        MissionFrame missionFrame = missionSaver.getMissionFrameAt(pos);
        Mission mission = new SpecificMission(missionFrame.getMissionName());
        missionAdapterListener.missionClicked(mission);
    }

    public interface MissionAdapterListener{
        void missionClicked(Mission mission);
    }

    class MissionHolder extends RecyclerView.ViewHolder {
        TextView mTitle;
        TextView mType;
        TextView description;

        MissionHolder(@NonNull View itemView){
            super(itemView);
            mTitle = itemView.findViewById(R.id.title);
            mType = itemView.findViewById(R.id.mtype);
            description = itemView.findViewById(R.id.description);
        }
    }
}
