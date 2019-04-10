package com.leidossd.dronecontrollerapp;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Locale;

import dji.common.camera.SettingsDefinitions;
import dji.common.camera.SettingsDefinitions.CameraMode;
import dji.common.camera.SystemState;
import dji.common.error.DJIError;
import dji.common.product.Model;
import dji.common.util.CommonCallbacks;
import dji.sdk.base.BaseProduct;
import dji.sdk.camera.Camera;
import dji.sdk.camera.VideoFeeder;
import dji.sdk.camera.VideoFeeder.VideoDataListener;
import dji.sdk.codec.DJICodecManager;

import static com.leidossd.dronecontrollerapp.MainApplication.showToast;

public class LiveVideoFragment extends Fragment implements
        SurfaceTextureListener, OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = LiveVideoFragment.class.getName();
    protected VideoFeeder.VideoDataListener videoDataListener = null;

    protected DJICodecManager codecManager = null;
    private VideoFeeder.VideoFeed videoFeed;

    protected TextureView videoTextureView = null;

    private boolean recording;
    private static CameraMode currentCameraMode;
    private TextView recordingTime;
    private ImageButton cameraCaptureButton;

    private Handler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new Handler();
        recording = false;
        currentCameraMode = CameraMode.SHOOT_PHOTO;

        // The callback for receiving the raw H264 video data for camera live view
        videoDataListener = new VideoDataListener() {

            @Override
            public void onReceive(byte[] videoBuffer, int size) {
                if (codecManager != null) {
                    codecManager.sendDataToDecoder(videoBuffer, size);
                }
            }
        };

        Camera camera = MainApplication.getCameraInstance();

        if (camera != null) {
            Log.d(TAG, "Setting camera system state callback");

            videoFeed = VideoFeeder.getInstance().getPrimaryVideoFeed();

            camera.setSystemStateCallback(new SystemState.Callback() {
                @Override
                public void onUpdate(@NonNull SystemState cameraSystemState) {
                    int recordTime = cameraSystemState.getCurrentVideoRecordingTimeInSeconds();
                    int minutes = (recordTime % 3600) / 60;
                    int seconds = recordTime % 60;

                    final String timeString = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
                    final boolean isVideoRecording = cameraSystemState.isRecording();

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                recordingTime.setText(timeString);

                                /*
                                 * Update recordingTime TextView visibility and mRecordBtn's check state
                                 */
                                if (isVideoRecording) {
                                    recordingTime.setVisibility(View.VISIBLE);
                                } else {
                                    recordingTime.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                    }
                }
            });
        } else {
            Log.d(TAG, "No camera found");
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_live_video, container, false);

        // init videoTextureView
        videoTextureView = view.findViewById(R.id.video_previewer_surface);

        if (null != videoTextureView) {
            videoTextureView.setSurfaceTextureListener(this);
        }

        // capture control components
        Switch captureModeSwitch = view.findViewById(R.id.switch_capture_mode);
        cameraCaptureButton = view.findViewById(R.id.btn_capture);
        recordingTime = view.findViewById(R.id.timer);

        captureModeSwitch.setOnCheckedChangeListener(this);
        cameraCaptureButton.setOnClickListener(this);
        recordingTime.setVisibility(View.INVISIBLE);

        return view;
    }

    protected void onProductChange() {
        initPreviewer();
    }

    private void initPreviewer() {

        BaseProduct product = MainApplication.getDroneInstance();

        if (product == null || !product.isConnected()) {
            showToast(getString(R.string.activity_droneconnection_statusDisconnected));
        } else {
            if (null != videoTextureView) {
                videoTextureView.setSurfaceTextureListener(this);
            }
            if (!product.getModel().equals(Model.UNKNOWN_AIRCRAFT)) {
                videoFeed.addVideoDataListener(videoDataListener);
            }
        }
    }

    private void uninitPreviewer() {
        Camera camera = MainApplication.getCameraInstance();
        if (camera != null) {
            // Reset the callback
            for (VideoDataListener v : videoFeed.getListeners()) {

            }
        }
    }

    @Override
    public void onClick(View view) {
        // might add components in future so leaving as switch for now
        switch (view.getId()) {
            case R.id.btn_capture: {
                if (currentCameraMode == CameraMode.SHOOT_PHOTO) {
                    capturePhoto();
                } else if (currentCameraMode == CameraMode.RECORD_VIDEO) {
                    if (!recording) {
                        startRecording();
                    } else {
                        stopRecording();
                    }
                }
                break;
            }
            default:
                showToast("Default");
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton button, boolean isChecked) {
        if (button.getId() == R.id.switch_capture_mode) {
            if (isChecked) {
                currentCameraMode = CameraMode.RECORD_VIDEO;
                cameraCaptureButton.setImageResource(R.drawable.ic_video_record_button);
            } else {
                currentCameraMode = CameraMode.SHOOT_PHOTO;
                cameraCaptureButton.setImageResource(R.drawable.ic_camera_button);
            }
            switchCameraMode();
        }
    }

    private void switchCameraMode() {
        Camera camera = MainApplication.getCameraInstance();
        if (camera != null) {
            camera.setMode(currentCameraMode, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError error) {

                    if (error == null) {
                        showToast("Switch Camera Mode Succeeded");
                    } else {
                        showToast(error.getDescription());
                    }
                }
            });
        }
    }

    // Method for taking photo
    private void capturePhoto() {
        final Camera camera = MainApplication.getCameraInstance();
        if (camera != null) {

            SettingsDefinitions.ShootPhotoMode photoMode = SettingsDefinitions.ShootPhotoMode.SINGLE; // Set the camera capture mode as Single mode
            camera.setShootPhotoMode(photoMode, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (null == djiError) {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                camera.startShootPhoto(new CommonCallbacks.CompletionCallback() {
                                    @Override
                                    public void onResult(DJIError djiError) {
                                        if (djiError == null) {
                                            showToast("take photo: success");
                                        } else {
                                            showToast(djiError.getDescription());
                                        }
                                    }
                                });
                            }
                        }, 2000);
                    }
                }
            });
        } else {
            showToast("No camera");
        }
    }

    private void startRecording() {
        final Camera camera = MainApplication.getCameraInstance();
        if (camera != null) {
            camera.startRecordVideo(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        recording = true;
                        showToast("Record video: success");
                    } else {
                        recording = false;
                        showToast(djiError.getDescription());
                    }
                }
            }); // Execute the startRecordVideo API
        }
    }

    private void stopRecording() {
        Camera camera = MainApplication.getCameraInstance();
        if (camera != null) {
            camera.stopRecordVideo(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        recording = false;
                        showToast("Stop recording: success");
                    } else {
                        recording = true;
                        showToast(djiError.getDescription());
                    }
                }
            });
        }
    }

    @Override
    public void onResume() {
        Log.e(TAG, "onResume");
        super.onResume();
        initPreviewer();
        onProductChange();

        if (videoTextureView == null) {
            Log.e(TAG, "videoTextureView is null");
        }
    }

    @Override
    public void onPause() {
        Log.e(TAG, "onPause");
        uninitPreviewer();
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.e(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        uninitPreviewer();
        super.onDestroy();
    }

    // Methods implementing SurfaceTextureListener

    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.e(TAG, "onSurfaceTextureAvailable");
        if (codecManager == null) {
            codecManager = new DJICodecManager(getActivity(), surface, width, height);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.e(TAG, "onSurfaceTextureSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.e(TAG, "onSurfaceTextureDestroyed");
        if (codecManager != null) {
            codecManager.cleanSurface();
            codecManager = null;
        }

        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }
}
