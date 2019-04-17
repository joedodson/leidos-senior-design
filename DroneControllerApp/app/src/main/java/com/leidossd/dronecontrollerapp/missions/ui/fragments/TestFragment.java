package com.leidossd.dronecontrollerapp.missions.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.leidossd.dronecontrollerapp.R;
import com.leidossd.dronecontrollerapp.missions.TestMission;
import com.leidossd.dronecontrollerapp.missions.ui.MissionCreateListener;

public class TestFragment extends Fragment {
    private MissionCreateListener testFragmentListener;
    private View.OnClickListener createButtonListener;

    private EditText missionName;
    private CheckBox saveCheckbox;
    private TextView taskListTextView;

    public TestFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createButtonListener = view -> {
            TestMission mission;
            String mName = missionName.getText().toString();
            if (mName.equals(""))
                mission = new TestMission();
            else
                mission = new TestMission(mName);
            testFragmentListener.createMission(mission, saveCheckbox.isChecked());
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_test, container, false);

        Button createButton = view.findViewById(R.id.button_create);
        missionName = view.findViewById(R.id.mission_name);
        saveCheckbox = view.findViewById(R.id.mission_save);

        taskListTextView = view.findViewById(R.id.tv_task_list);
        String taskListString = "";
        TestMission mission = new TestMission("Test Mission");
        int i = 0;
        for (String taskName : mission.getTaskNames()) {
            taskListString = String.format("%s\n%s: %s", taskListString, ++i, taskName);
        }
        taskListTextView.setText(taskListString);

        createButton.setOnClickListener(createButtonListener);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MissionCreateListener) {
            testFragmentListener = (MissionCreateListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement MissionCreateListener");
        }
    }
}
