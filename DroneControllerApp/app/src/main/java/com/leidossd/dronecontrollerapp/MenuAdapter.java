package com.leidossd.dronecontrollerapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MenuAdapter extends Adapter<MenuAdapter.MenuHolder> {
    private ArrayList<Pair<String, String>> menuOptions;
    private MenuListener menuListener;

    public MenuAdapter(Fragment fragment, ArrayList<Pair<String, String>> menuOptions) {
        if (fragment instanceof MenuListener) {
            this.menuListener = (MenuListener) fragment;
        } else {
            throw new RuntimeException(fragment.toString()
                    + " must implement MenuListener");
        }
        this.menuOptions = menuOptions;
    }

    @NonNull
    @Override
    public MenuHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int pos) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.menu_rows, viewGroup, false);
        MenuHolder vh = new MenuHolder(v);
        v.setOnClickListener((View view) -> {
            int p = vh.getAdapterPosition()+1;
            menuListener.menuClicked(p);
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MenuHolder viewHolder, int pos) {
        viewHolder.title.setText(menuOptions.get(pos).first);
        viewHolder.description.setText(menuOptions.get(pos).second);
    }

    @Override
    public int getItemCount() {
        if(menuOptions != null) {
            return menuOptions.size();
        }
        return 0;
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
