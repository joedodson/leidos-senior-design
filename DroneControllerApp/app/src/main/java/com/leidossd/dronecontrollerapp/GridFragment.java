package com.leidossd.dronecontrollerapp;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leidossd.djiwrapper.Coordinate;
import com.leidossd.djiwrapper.FlightControllerWrapper;


public class GridFragment extends Fragment {
    private GridInteractionListener gridInteractionListener;
    private OnClickListener gridSelectListener;
    private FlightControllerWrapper flightControllerWrapper;

    private TextView currentPosition;

    private static final float MOVEMENT_MULTI = 1.0f;

    public GridFragment() { }

    float x, y, z;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x = y = z = 0;

        gridSelectListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                Coordinate coordinate = null;
                int id = view.getId();
                switch(id) {
                    case R.id.grid_nw:
                        x -= MOVEMENT_MULTI;
                        y += MOVEMENT_MULTI;
                        break;
                    case R.id.grid_n:
                        y += MOVEMENT_MULTI;
                        break;
                    case R.id.grid_ne:
                        x += MOVEMENT_MULTI;
                        y += MOVEMENT_MULTI;
                        break;
                    case R.id.grid_w:
                        x -= MOVEMENT_MULTI;
                        break;
                    case R.id.grid_center:
                        x = y = z = 0;
                        break;
                    case R.id.grid_e:
                        x += MOVEMENT_MULTI;
                        break;
                    case R.id.grid_sw:
                        x -= MOVEMENT_MULTI;
                        y -= MOVEMENT_MULTI;
                        break;
                    case R.id.grid_s:
                        y -= MOVEMENT_MULTI;
                        break;
                    case R.id.grid_se:
                        x += MOVEMENT_MULTI;
                        y -= MOVEMENT_MULTI;
                        break;
                    default:
                        return;
                }
                gridInteractionListener.sendInput(new Coordinate(x,y,z));
            }
        };

        scheduleTextUpdate();
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
        currentPosition = view.findViewById(R.id.current_position);
        flightControllerWrapper = FlightControllerWrapper.getInstance();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof GridInteractionListener) {
            gridInteractionListener = (GridInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement WaypointFragmentListener");
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

    private void scheduleTextUpdate() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(flightControllerWrapper != null) {
                    currentPosition.setText("x: " + flightControllerWrapper.getPosition().getX() +
                            ", y: " + flightControllerWrapper.getPosition().getY() +
                            ", z: " + flightControllerWrapper.getPosition().getZ());
                }
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }
}
