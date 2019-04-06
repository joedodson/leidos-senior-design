package com.leidossd.dronecontrollerapp.simulator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.leidossd.dronecontrollerapp.MainApplication;
import com.leidossd.dronecontrollerapp.R;

import java.util.Timer;
import java.util.TimerTask;

import dji.common.error.DJIError;
import dji.common.flightcontroller.virtualstick.FlightControlData;
import dji.common.flightcontroller.virtualstick.FlightCoordinateSystem;
import dji.common.flightcontroller.virtualstick.RollPitchControlMode;
import dji.common.flightcontroller.virtualstick.VerticalControlMode;
import dji.common.flightcontroller.virtualstick.YawControlMode;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;

import static com.leidossd.dronecontrollerapp.MainApplication.showToast;
import static com.leidossd.utils.DroneConnectionStatus.DRONE_CONNECTED;
import static com.leidossd.utils.IntentAction.CONNECTION_CHANGE;


public class SimulatorActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = SimulatorActivity.class.getName();

    private FlightController flightController;

    private TextView telemetryTextView;
    private Timer sendVirtualStickDataTimer;
    private SendVirtualStickDataTask sendVirtualStickDataTask;
    private Button takeOffButton;
    private Button landButton;
    private ToggleButton startSimulatorButton;

    private float pitch;
    private float roll;
    private float yaw;
    private float throttle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulator);

        BroadcastReceiver connectionChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent connectionChangeIntent) {
                String droneStatus = connectionChangeIntent.getStringExtra(CONNECTION_CHANGE.getResultKey());
                if (!droneStatus.equals(DRONE_CONNECTED.toString())) {
                    finish();
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(connectionChangeReceiver, new IntentFilter(CONNECTION_CHANGE.getActionString()));

        initUI();
    }

    private void initFlightController() {
        Aircraft aircraft = MainApplication.getDroneInstance();
        if (aircraft == null || !aircraft.isConnected()) {
            showToast("Disconnected");
            flightController = null;
        } else {
            flightController = aircraft.getFlightController();
            flightController.setRollPitchControlMode(RollPitchControlMode.VELOCITY);
            flightController.setYawControlMode(YawControlMode.ANGULAR_VELOCITY);
            flightController.setVerticalControlMode(VerticalControlMode.VELOCITY);
            flightController.setRollPitchCoordinateSystem(FlightCoordinateSystem.BODY);

//            flightController.getSimulator().setStateCallback(new SimulatorState.Callback() {
//                @Override
//                public void onUpdate(@NonNull final SimulatorState stateData) {
//                    new Handler(Looper.getMainLooper()).post(new Runnable() {
//                        @Override
//                        public void run() {
//                            float yaw = stateData.getYaw();
//                            float pitch = stateData.getPitch();
//                            float roll = stateData.getRoll();
//                            float positionX = stateData.getPositionX();
//                            float positionY = stateData.getPositionY();
//                            float positionZ = stateData.getPositionZ();
//
//                            telemetryTextView.setText(String.format(getString(R.string.simulator_telemetry),
//                                    yaw, pitch, roll, positionX, positionY, positionZ));
//                        }
//                    });
//                }
//            });
        }
    }

    private void initUI() {
        OnScreenJoystick rightJoystick;
        OnScreenJoystick leftJoystick;

        takeOffButton = findViewById(R.id.btn_take_off);
        landButton = findViewById(R.id.btn_land);
        startSimulatorButton = findViewById(R.id.btn_start_simulator);
        telemetryTextView = findViewById(R.id.textview_simulator);
        rightJoystick = findViewById(R.id.directionJoystickRight);
        leftJoystick = findViewById(R.id.directionJoystickLeft);

        takeOffButton.setOnClickListener(this);
        landButton.setOnClickListener(this);

        startSimulatorButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (flightController != null) {
                    if (isChecked) {
                        enableSimulator();
                    } else {
                        disableSimulator();
                    }
                }
            }
        });

        rightJoystick.setJoystickListener(new OnScreenJoystickListener() {

            @Override
            public void onTouch(OnScreenJoystick joystick, float pX, float pY) {
                if (Math.abs(pX) < 0.02) {
                    pX = 0;
                }

                if (Math.abs(pY) < 0.02) {
                    pY = 0;
                }

                float pitchJoyControlMaxSpeed = 10;
                float rollJoyControlMaxSpeed = 10;

                pitch = pitchJoyControlMaxSpeed * pX;

                roll = rollJoyControlMaxSpeed * pY;

                if (null == sendVirtualStickDataTimer) {
                    sendVirtualStickDataTask = new SendVirtualStickDataTask();

                    sendVirtualStickDataTimer = new Timer();
                    sendVirtualStickDataTimer.schedule(sendVirtualStickDataTask, 100, 200);
                }

            }

        });

        leftJoystick.setJoystickListener(new OnScreenJoystickListener() {

            @Override
            public void onTouch(OnScreenJoystick joystick, float pX, float pY) {
                if (Math.abs(pX) < 0.02) {
                    pX = 0;
                }

                if (Math.abs(pY) < 0.02) {
                    pY = 0;
                }
                float verticalJoyControlMaxSpeed = 2;
                float yawJoyControlMaxSpeed = 30;

                yaw = yawJoyControlMaxSpeed * pX;
                throttle = verticalJoyControlMaxSpeed * pY;

                if (null == sendVirtualStickDataTimer) {
                    sendVirtualStickDataTask = new SendVirtualStickDataTask();
                    sendVirtualStickDataTimer = new Timer();
                    sendVirtualStickDataTimer.schedule(sendVirtualStickDataTask, 0, 200);
                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is NOT part of this app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(this)
                            // Add all of this activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                            // Navigate up to the closest parent
                            .startActivities();
                } else {
                    // This activity is part of this app's task, so simply
                    // navigate up to the logical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_take_off:
                if (flightController != null) {
                    flightController.startTakeoff(new DjiSimulatorTaskCallback("Take off"));
                }
                break;

            case R.id.btn_land:
                if (flightController != null) {
                    flightController.startLanding(new DjiSimulatorTaskCallback("Landing"));
                }
                break;

            default:
                break;
        }
    }

    class SendVirtualStickDataTask extends TimerTask {

        @Override
        public void run() {

            if (flightController != null) {
                flightController.sendVirtualStickFlightControlData(
                        new FlightControlData(
                                pitch, roll, yaw, throttle
                        ), new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                            }
                        }
                );
            }
        }
    }

    @Override
    public void onResume() {
        Log.v(TAG, "onResume");
        super.onResume();
        initFlightController();

    }

    @Override
    public void onPause() {
        Log.v(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.v(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG, "onDestroy");
        if (null != sendVirtualStickDataTimer) {
            sendVirtualStickDataTask.cancel();
            sendVirtualStickDataTask = null;
            sendVirtualStickDataTimer.cancel();
            sendVirtualStickDataTimer.purge();
            sendVirtualStickDataTimer = null;
        }
        super.onDestroy();
    }

    private void enableSimulator() {
        takeOffButton.setEnabled(true);
        landButton.setEnabled(true);
        startSimulatorButton.setChecked(true);

        flightController.setVirtualStickModeEnabled(true,
                new DjiSimulatorTaskCallback("Enable Virtual Sticks"));

        //flightController.getSimulator()
        //        .start(InitializationData.createInstance(
        //                new LocationCoordinate2D(23, 113), 10, 10),
        //                new DjiSimulatorTaskCallback("Start Simulator"));
    }

    private void disableSimulator() {
        takeOffButton.setEnabled(false);
        landButton.setEnabled(false);
        startSimulatorButton.setChecked(false);

        telemetryTextView.setText(getString(R.string.simulator_status_off));

        flightController.setVirtualStickModeEnabled(false,
                new DjiSimulatorTaskCallback("Disable Virtual Sticks"));
        //flightController.getSimulator()
        //        .stop(new DjiSimulatorTaskCallback("Stop Simulator"));
    }

    class DjiSimulatorTaskCallback implements CommonCallbacks.CompletionCallback {

        private final String TASK_NAME;

        DjiSimulatorTaskCallback(String taskName) {
            TASK_NAME = "Simulator - " + taskName;
        }

        @Override
        public void onResult(DJIError djiError) {
            if (djiError != null) {
                showToast(TASK_NAME + " ERROR: " + djiError.getDescription());
            } else {
                showToast(TASK_NAME + ": Success");
            }
        }
    }
}
