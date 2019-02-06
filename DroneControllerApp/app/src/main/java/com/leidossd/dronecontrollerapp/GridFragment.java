package com.leidossd.dronecontrollerapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.leidossd.djiwrapper.Coordinate;


public class GridFragment extends Fragment {
    private GridInteractionListener gridInteractionListener;
    private OnClickListener gridSelectListener;

    private static final float MOVEMENT_MULTI = 1.0f;

    public GridFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gridSelectListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                Coordinate coordinate = null;
                int id = view.getId();
                switch(id) {
                    case R.id.grid_nw:
                        coordinate = new Coordinate(-MOVEMENT_MULTI,MOVEMENT_MULTI,0);
                        break;
                    case R.id.grid_n:
                        coordinate = new Coordinate(0,MOVEMENT_MULTI,0);
                        break;
                    case R.id.grid_ne:
                        coordinate = new Coordinate(MOVEMENT_MULTI,MOVEMENT_MULTI,0);
                        break;
                    case R.id.grid_w:
                        coordinate = new Coordinate(-MOVEMENT_MULTI,0,0);
                        break;
                    case R.id.grid_center:
                        coordinate = new Coordinate(0,0,0);
                        break;
                    case R.id.grid_e:
                        coordinate = new Coordinate(MOVEMENT_MULTI,0,0);
                        break;
                    case R.id.grid_sw:
                        coordinate = new Coordinate(-MOVEMENT_MULTI,-MOVEMENT_MULTI,0);
                        break;
                    case R.id.grid_s:
                        coordinate = new Coordinate(0,-MOVEMENT_MULTI,0);
                        break;
                    case R.id.grid_se:
                        coordinate = new Coordinate(MOVEMENT_MULTI,-MOVEMENT_MULTI,0);
                        break;
                    default:
                        return;
                }
                gridInteractionListener.sendInput(coordinate);
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_grid, container, false);

        int[] buttons = {R.id.grid_nw, R.id.grid_n, R.id.grid_ne,
                R.id.grid_w, R.id.grid_center, R.id.grid_e,
                R.id.grid_sw, R.id.grid_s, R.id.grid_se};

        for (int i : buttons){
            view.findViewById(i).setOnClickListener(gridSelectListener);
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof GridInteractionListener) {
            gridInteractionListener = (GridInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        gridInteractionListener = null;
    }

    public interface GridInteractionListener {
        void sendInput(Coordinate coordinate);
    }
}
