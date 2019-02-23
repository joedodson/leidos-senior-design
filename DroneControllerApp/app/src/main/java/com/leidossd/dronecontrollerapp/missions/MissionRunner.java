package com.leidossd.dronecontrollerapp.missions;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.leidossd.dronecontrollerapp.MainApplication;
import com.leidossd.dronecontrollerapp.MissionStatusActivity;
import com.leidossd.dronecontrollerapp.R;
import com.leidossd.dronecontrollerapp.missions.MissionRunnerService.ServiceStatusUpdate;

import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class MissionRunner {
    private static final String TAG = MissionRunner.class.getName();

    private static MissionRunnerService missionRunnerService = null;
    private static AtomicBoolean missionRunnerServiceIsBound = new AtomicBoolean(false);
    private static AtomicBoolean serviceBindingInProgress = new AtomicBoolean(false);
    private static AtomicBoolean missionInProgress = new AtomicBoolean(false);

    private NotificationManagerCompat notificationManager;
    private NotificationCompat.Builder notificationBuilder;
    private int notificationId;
    private static final long notificationUpdateIntervalMs = 1000;
    private Handler handler;
    private Timer timer;
    private TimerTask updateNotificationTask;
    private long missionStartTime = 0;

    private static LocalBroadcastManager localBroadcastManager;

    private static final String MISSION_BUNDLE_EXTRA_NAME = "MISSION_EXTRA";

    public MissionRunner(Context applicationContext) {
        Random random = new Random();
        notificationId = random.nextInt(1000) + 1;
        notificationManager = NotificationManagerCompat.from(applicationContext);
        handler = new Handler();

        if (!serviceBindingInProgress.get()) {
            serviceBindingInProgress.set(true);
            bindService(applicationContext);
        }
    }

    private static synchronized void bindService(Context applicationContext) {
        Intent intent = new Intent(applicationContext, MissionRunnerService.class);

        ServiceConnection missionRunnerServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {

                // We've bound to LocalService, cast the IBinder and get LocalService instance
                MissionRunnerService.MissionRunnerBinder binder = (MissionRunnerService.MissionRunnerBinder) service;
                missionRunnerService = binder.getService();
                missionRunnerServiceIsBound.set(true);
                serviceBindingInProgress.set(false);
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                missionRunnerServiceIsBound.set(false);
                serviceBindingInProgress.set(false);
            }
        };
        applicationContext.bindService(intent, missionRunnerServiceConnection, Context.BIND_AUTO_CREATE);
        localBroadcastManager = LocalBroadcastManager.getInstance(applicationContext);
    }

    public synchronized void startMission(Context applicationContext, Mission mission) {
        if (!missionRunnerServiceIsBound.get()) {
            mission.getMissionUpdateCallback().onMissionError("MissionRunner unable to bind to service");
        } else if (!missionInProgress.compareAndSet(false, true)) {
            mission.getMissionUpdateCallback().onMissionError("Mission already in progress");
        } else {

            Intent missionIntent = new Intent(applicationContext, MissionRunnerService.class);

            if (mission.getMissionUpdateCallback() != null) {
                registerReceivers(mission);
            }

            missionIntent.putExtra(MISSION_BUNDLE_EXTRA_NAME, mission);

            Intent notificationIntent = new Intent(applicationContext, MissionStatusActivity.class);
            notificationIntent.putExtra(MISSION_BUNDLE_EXTRA_NAME, mission);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(applicationContext, 0, notificationIntent, 0);

            notificationBuilder =
                    new NotificationCompat.Builder(applicationContext, applicationContext.getResources().getString(R.string.notification_channel_name))
                            .setContentTitle("Mission Runner")
                            .setContentText(String.valueOf(System.currentTimeMillis()))
                            .setSmallIcon(R.drawable.rsz_rsz_drone_icon)
                            .setContentIntent(pendingIntent)
                            .setTicker("New Mission");

            missionStartTime = System.currentTimeMillis();
            missionRunnerService.startForegroundService(missionIntent);
            missionRunnerService.startForeground(notificationId, notificationBuilder.build());

            updateNotificationTask = new TimerTask() {
                @Override
                public void run() {
                    handler.post(() -> {
                        long runningTime = System.currentTimeMillis() - missionStartTime;
                        Mission currentMission = missionRunnerService.getCurrentMission();
                        String formattedRunningTime;
                        if (currentMission != null) {
                            formattedRunningTime = String.format(Locale.US, "%s %s %d:%02d",
                                    currentMission.getTitle(), currentMission.getStatus(),
                                    (runningTime / 1000) / 60,
                                    (int) ((runningTime / 1000) % 60));
                        } else {
                            formattedRunningTime = "Mission starting, please wait";
                        }

                        notificationManager.notify(notificationId, notificationBuilder
                                .setContentText(formattedRunningTime).build());
                    });
                }
            };
            timer = new Timer();
            timer.schedule(updateNotificationTask, 100, notificationUpdateIntervalMs);
        }
    }

    private void registerReceivers(Mission mission) {
        localBroadcastManager.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mission.getMissionUpdateCallback().onMissionStart(intent.getStringExtra(ServiceStatusUpdate.getResultKey()));
            }
        }, new IntentFilter(ServiceStatusUpdate.MISSION_START.action));

        localBroadcastManager.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                clean();
                notificationManager.notify(notificationId,
                        notificationBuilder.setContentText(
                                String.format("%s %s",
                                        missionRunnerService.getCurrentMission().getTitle(),
                                        missionRunnerService.getCurrentMission().getStatus()))
                                .build());
                mission.getMissionUpdateCallback().onMissionError("from runner " + intent.getStringExtra(ServiceStatusUpdate.getResultKey()));
            }
        }, new IntentFilter(ServiceStatusUpdate.MISSION_FINISH.action));

        localBroadcastManager.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mission.getMissionUpdateCallback().onMissionError(intent.getStringExtra(ServiceStatusUpdate.getResultKey()));
            }
        }, new IntentFilter(ServiceStatusUpdate.MISSION_ERROR.action));
    }

    private void clean() {
        missionRunnerService.stopForeground(true);
        timer.cancel();
        handler.removeCallbacksAndMessages(null);
        missionRunnerService.stopSelf();
        missionInProgress.set(false);
    }
}

