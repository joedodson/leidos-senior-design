package com.leidossd.dronecontrollerapp.missions;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import com.leidossd.dronecontrollerapp.MissionStatusActivity;
import com.leidossd.dronecontrollerapp.R;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MissionRunner {

    private static MissionRunnerService missionRunnerService;
    private static boolean missionRunnerServiceIsBound = false;

    private static MissionRunner missionRunnerInstance;
    private static NotificationManagerCompat notificationManager;
    private static int notificationId;
    private static final long notificationUpdateIntervalMs = 1000;
    private static Timer timer;
    private long missionStartTime = 0;

    private class MissionRunnerUpdateCallback implements Mission.MissionUpdateCallback, Parcelable {
        @Override
        public void onMissionStart(String missionStartResult) {
            Toast.makeText(missionRunnerService.getApplicationContext(), "mission started", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onMissionFinish(String missionFinishResult) {
            Toast.makeText(missionRunnerService.getApplicationContext(), "mission stopped", Toast.LENGTH_LONG).show();
        }

        @Override
        public int describeContents(){
            return 0;
        }

        MissionRunnerUpdateCallback() {}

        // Parcelable functionality
        MissionRunnerUpdateCallback(Parcel in) {}

        @Override
        public void writeToParcel(Parcel dest, int flags) { }

        public final Parcelable.Creator CREATOR = new Parcelable.Creator() {
            public MissionRunnerUpdateCallback createFromParcel(Parcel in) {
                return new MissionRunnerUpdateCallback(in);
            }

            public MissionRunnerUpdateCallback[] newArray(int size) {
                return new MissionRunnerUpdateCallback[size];
            }
        };
    }

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

        mission.setMissionUpdateCallback(new MissionRunnerUpdateCallback());
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
                        Mission currentMission = missionRunnerService.getCurrentMission();
                        String formattedRunningTime;
                        if(currentMission != null) {
                            formattedRunningTime = String.format("%s %s %d:%02d",
                                    currentMission.getTitle(), currentMission.getStatus(),
                                    (runningTime / 1000) / 60,
                                    (int)((runningTime / 1000) % 60));
                        } else {
                            formattedRunningTime = "Mission starting, please wait";
                        }

                        notificationManager.notify(notificationId, notificationBuilder
                                .setContentText(formattedRunningTime).build());
                    }
                });
            }
        };
        timer.schedule(updateNotificationTask, 100, notificationUpdateIntervalMs);

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
                                notificationBuilder.setContentText(String.format("%s %s", missionRunnerService.getCurrentMission().getTitle(), missionRunnerService.getCurrentMission().getStatus())).build());
                    }
                }, 500);
            }
        }, 10000);
    }
}

