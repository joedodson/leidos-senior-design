package com.leidossd.dronecontrollerapp.missions.execution;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.leidossd.dronecontrollerapp.R;
import com.leidossd.dronecontrollerapp.missions.Mission;
import com.leidossd.dronecontrollerapp.missions.Task;
import com.leidossd.dronecontrollerapp.missions.ui.MissionStatusActivity;

import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Purpose of the class is simplify usage of the MissionRunnerService and help protect misuse of the
 * service. Provides simple API to use everywhere else in the app.
 */
public class MissionRunner {
    private static final String TAG = MissionRunner.class.getSimpleName();
    private static MissionRunnerService missionRunnerService = null;

    /**
     * Static atomics are the best way around Singleton pattern for now. Multiple mission runners
     * can be instantiated, but static atomics help prevent them from running more than one mission at
     * a time, and prevent race conditions.
     */
    private static AtomicBoolean missionRunnerServiceIsBound = new AtomicBoolean(false);
    private static AtomicBoolean serviceBindingInProgress = new AtomicBoolean(false);
    private static AtomicBoolean missionInProgress = new AtomicBoolean(false);

    private static NotificationManagerCompat notificationManager;
    private static NotificationCompat.Builder notificationBuilder;
    private static int notificationId;
    private static Timer timer;
    private static final long notificationUpdateIntervalMs = 1000;
    private static Handler handler;
    private long missionStartTime = 0;

    private static final Task.StatusUpdateListener mListener = (newStatus, message) -> {
        if(newStatus == Task.TaskState.COMPLETED || newStatus == Task.TaskState.FAILED) {
            notificationManager.notify(notificationId,
                    notificationBuilder.setContentText(
                            String.format("%s %s",
                                    missionRunnerService.getCurrentMission().getTitle(),
                                    newStatus.toString()))
                            .build());
            clean();
        }
    };

    private static final String MISSION_BUNDLE_EXTRA_NAME = "MISSION_EXTRA";

    public MissionRunner(Context applicationContext) {
        notificationManager = NotificationManagerCompat.from(applicationContext);
        handler = new Handler();

        if (!serviceBindingInProgress.get()) {
            serviceBindingInProgress.set(true);
            bindService(applicationContext.getApplicationContext());
        }
    }

    /**
     * Binds MissionRunner object to the MissionRunnerService after instantiation. Synchronized to
     * prevent race conditions because binding is asynchronous.
     *
     * @param applicationContext Context from activity or application
     */
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
    }

    /**
     * Main driver for running the actual mission. Synchronized to prevent race conditions of multiple
     * MissionRunners trying to start missions.
     *
     * @param applicationContext Context from activity or application
     * @param mission            The mission to be executed
     */
    public synchronized void startMission(Context applicationContext, Mission mission, Task.StatusUpdateListener listener) {
        if (!missionRunnerServiceIsBound.get()) {
            listener.statusUpdate(Task.TaskState.FAILED, "MissionRunner unable to bind to service");
        } else if (!missionInProgress.compareAndSet(false, true)) {
            listener.statusUpdate(Task.TaskState.FAILED, "Mission already in progress");
        } else {

            // Put mission into an intent as an extra (make sure the mission implements Parcelable correctly
            Intent missionIntent = new Intent(applicationContext, MissionRunnerService.class);

            missionIntent.putExtra(MISSION_BUNDLE_EXTRA_NAME, mission);

            // Foreground services HAVE to display notification to user while running
            Intent notificationIntent = new Intent(applicationContext, MissionStatusActivity.class);
            notificationIntent.putExtra(MISSION_BUNDLE_EXTRA_NAME, mission);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(applicationContext, 0, notificationIntent, 0);

            Random random = new Random();
            notificationId = random.nextInt(1000) + 1;
            notificationBuilder =
                    new NotificationCompat.Builder(applicationContext, applicationContext.getResources().getString(R.string.notification_channel_name))
                            .setContentTitle("Mission Runner")
                            .setContentText(String.valueOf(System.currentTimeMillis()))
                            .setSmallIcon(R.drawable.rsz_rsz_drone_icon)
                            .setContentIntent(pendingIntent)
                            .setTicker("New Mission")
                            .setOnlyAlertOnce(true);

            missionStartTime = System.currentTimeMillis();
            //Added in version check for testing purposes.  Can be removed once version is finalized.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                missionRunnerService.startForegroundService(missionIntent);
            } else {
                missionRunnerService.startService(missionIntent);
            }
            missionRunnerService.startForeground(notificationId, notificationBuilder.build());

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(missionRunnerService.mission != null) {
                        if(listener != null) {
                            missionRunnerService.mission.addListener(listener);
                            listener.statusUpdate(missionRunnerService.mission.getCurrentState(), null);
                        }

                        missionRunnerService.mission.addListener(mListener);
                    } else {
                        handler.postDelayed(this, 100);
                    }
                }
            }, 100);

            // Repeatedly updates the notification with mission run time
            TimerTask updateNotificationTask = new TimerTask() {
                @Override
                public void run() {
                    handler.post(() -> {
                        long runningTime = System.currentTimeMillis() - missionStartTime;
                        Mission currentMission = missionRunnerService.getCurrentMission();
                        String formattedRunningTime;
                        if (currentMission != null) {
                            formattedRunningTime = String.format(Locale.US, "%s %s %d:%02d",
                                    currentMission.getTitle(), currentMission.getCurrentState(),
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

    /**
     * Returns the currently active mission in service.
     * Returns copy so that mission listener can't be modified without using the setMissionListener method
     * @return copy of currently active mission or null if no active mission
     */
    public @Nullable Mission getCurrentMission() {
        if (missionRunnerService != null && missionRunnerService.mission != null) {
            return missionRunnerService.mission;
        }

        return null;
    }

    /**
     * For code readability. All tasks that should be done in between missions.
     */
    private static void clean() {
        missionRunnerService.stopForeground(false);
        missionRunnerService.stopSelf();

        timer.cancel();
        handler.removeCallbacksAndMessages(null);
        missionInProgress.set(false);
    }
}

