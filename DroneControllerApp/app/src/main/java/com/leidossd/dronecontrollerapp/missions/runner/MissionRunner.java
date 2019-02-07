package com.leidossd.dronecontrollerapp.missions.runner;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.leidossd.dronecontrollerapp.MainApplication;
import com.leidossd.dronecontrollerapp.MissionStatusActivity;
import com.leidossd.dronecontrollerapp.R;
import com.leidossd.dronecontrollerapp.missions.Mission;

import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.leidossd.dronecontrollerapp.MainApplication.showToast;

public class MissionRunner {

    private static MissionRunnerService missionRunnerService;
    private static boolean missionRunnerServiceIsBound = false;

    private static MissionRunner missionRunnerInstance;
    private static NotificationManagerCompat notificationManager;
    private static int notificationId;
    private static final long notificationUpdateIntervalMs = 1000;
    private static Timer timer;
    private long missionStartTime = 0;

    private static final String MISSION_BUNDLE_EXTRA_NAME = "MISSION_EXTRA";
    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private static ServiceConnection missionRunnerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MissionRunnerService.MissionRunnerBinder binder = (MissionRunnerService.MissionRunnerBinder) service;
            missionRunnerService = binder.getService();
            missionRunnerServiceIsBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            missionRunnerServiceIsBound = false;
        }
    };

    private MissionRunner() {
        Random random = new Random();
        notificationId = random.nextInt(1000) + 1;
    }

    public static MissionRunner getInstance(Context applicationContext) {
        if (missionRunnerInstance == null || !missionRunnerServiceIsBound) {
            Intent intent = new Intent(applicationContext, MissionRunnerService.class);

            applicationContext.bindService(intent, missionRunnerServiceConnection, Context.BIND_AUTO_CREATE);
            notificationManager = NotificationManagerCompat.from(applicationContext);
            timer = new Timer();
            missionRunnerInstance = new MissionRunner();
        }

        return missionRunnerInstance;
    }

    public void startMission(Context applicationContext, Mission mission) {
        Intent missionIntent = new Intent(applicationContext, MissionRunnerService.class);
        missionIntent.putExtra(MISSION_BUNDLE_EXTRA_NAME, mission);

        Intent notificationIntent = new Intent(applicationContext, MissionStatusActivity.class);
        notificationIntent.putExtra(MISSION_BUNDLE_EXTRA_NAME, mission);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(applicationContext, 0, notificationIntent, 0);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(applicationContext, applicationContext.getResources().getString(R.string.notification_channel_name))
                        .setContentTitle("Mission Runner")
                        .setContentText(String.valueOf(System.currentTimeMillis()))
                        .setSmallIcon(R.drawable.rsz_rsz_drone_icon)
                        .setContentIntent(pendingIntent)
                        .setTicker("New Mission");

        missionStartTime = System.currentTimeMillis();
        missionRunnerService.startForegroundService(missionIntent);
        missionRunnerService.startForeground(notificationId, notificationBuilder.build());

        Handler handler = new Handler();

        TimerTask updateNotificationTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        long runningTime = System.currentTimeMillis() - missionStartTime;
                        String formattedRunningTime = String.format("%s running %d:%02d",
                                mission.getTitle(),
                                (runningTime / 1000) / 60,
                                (int)((runningTime / 1000) % 60));

                        notificationManager.notify(notificationId, notificationBuilder
                                .setContentText(formattedRunningTime).build());
                    }
                });
            }
        };
        timer.schedule(updateNotificationTask, 0, notificationUpdateIntervalMs);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mission.stop();
                missionRunnerService.stopForeground(true);
                timer.cancel();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        notificationManager.notify(notificationId,
                                notificationBuilder.setContentText(mission.getTitle() + " completed").build());
                    }
                }, 500);
            }
        }, 10000);
    }
}

