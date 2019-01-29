package com.leidossd.dronecontrollerapp;

import android.content.Context;
import android.net.Uri;
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

    public GridFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gridSelectListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                Coordinate coordinate = null;
                switch(view.getId()) {
                    case R.id.grid_nw:
                        coordinate = new Coordinate(-1,-1,0);
                        break;
                    case R.id.grid_n:
                        coordinate = new Coordinate(-1,0,0);
                        break;
                    case R.id.grid_ne:
                        coordinate = new Coordinate(-1,1,0);
                        break;
                    case R.id.grid_w:
                        coordinate = new Coordinate(0,-1,0);
                        break;
                    case R.id.grid_center:
                        coordinate = new Coordinate(0,0,0);
                        break;
                    case R.id.grid_e:
                        coordinate = new Coordinate(0,1,0);
                        break;
                    case R.id.grid_sw:
                        coordinate = new Coordinate(1,-1,0);
                        break;
                    case R.id.grid_s:
                        coordinate = new Coordinate(1,0,0);
                        break;
                    case R.id.grid_se:
                        coordinate = new Coordinate(1,1,0);
                        break;
                    default:
                        break;
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

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface GridInteractionListener {
        // TODO: Update argument type and name
        void onGridInteraction(Uri uri);
    }
}
