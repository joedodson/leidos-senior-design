package com.leidossd.dronecontrollerapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MenuAdapter extends Adapter {
    private ArrayList<Pair<String, String>> menuOption;

    public MenuAdapter(Context context, ArrayList<>) {
        if (context instanceof MenuListener) {
            this.missionAdapterListener = (MenuListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement MissionAdapterListener");
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.saved_mission_rows, viewGroup, false);
        ViewHolder vh = new MenuHolder(v);
        v.setOnClickListener((View view) -> {
            int pos = vh.getAdapterPosition();
            sendMission(pos);
        });
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return menuOption.size();
    }

    public interface MenuListener{
        void menuClicked(int pos);
    }

    class MenuHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView description;

        MenuHolder(@NonNull View itemView){
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
        }
    }
}
