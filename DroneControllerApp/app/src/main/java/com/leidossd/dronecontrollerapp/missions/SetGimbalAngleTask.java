package com.leidossd.dronecontrollerapp.missions;

import android.os.Parcel;

import dji.common.gimbal.Rotation;
import dji.common.gimbal.RotationMode;
import dji.sdk.sdkmanager.DJISDKManager;

public class SetGimbalAngleTask extends Task {
    float angle;
    Rotation rotation;

    SetGimbalAngleTask(float angle) {
        super("Rotating gimbal to " + Float.toString(angle));
        this.angle = angle;

        // to learn about what the gimbal can do, you need to look at their documentation
        // get functions to ask the drone what it can do, then print those to see.

        // I had to do these, for example
//        Gimbal g = DJISDKManager.getInstance().getProduct().getGimbal();
//        Map<CapabilityKey,DJIParamCapability> c = g.getCapabilities();
//
//        showToast(Boolean.toString(c.get(CapabilityKey.ADJUST_PITCH).isSupported()));
//        showToast(((DJIParamMinMaxCapability) c.get(CapabilityKey.ADJUST_PITCH)).getMin().toString());
//        showToast(((DJIParamMinMaxCapability) c.get(CapabilityKey.ADJUST_PITCH)).getMax().toString());
//        showToast(Boolean.toString(c.get(CapabilityKey.ADJUST_ROLL).isSupported()));
//        showToast(((DJIParamMinMaxCapability) c.get(CapabilityKey.ADJUST_ROLL)).getMin().toString());
//        showToast(((DJIParamMinMaxCapability) c.get(CapabilityKey.ADJUST_ROLL)).getMax().toString());
//        showToast(Boolean.toString(c.get(CapabilityKey.ADJUST_YAW).isSupported()));
//        showToast(((DJIParamMinMaxCapability) c.get(CapabilityKey.ADJUST_YAW)).getMin().toString());
//        showToast(((DJIParamMinMaxCapability) c.get(CapabilityKey.ADJUST_YAW)).getMax().toString());

        // and what I learned was that the Mavic Air only supports pitch, and it goes from
        // -90 (looking straight down) to 30 (looking up a little bit), 0 is looking straight forward

        this.rotation = new Rotation.Builder().mode(RotationMode.ABSOLUTE_ANGLE)
                .pitch(angle).time(1).build();

        currentState = Task.TaskState.READY;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString("SET_GIMBAL_ANGLE_TASK");
        out.writeFloat(angle);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    static public SetGimbalAngleTask create(Parcel in){
        return new SetGimbalAngleTask(in.readFloat());
    }

    @Override
    void write(Parcel out){
        out.writeString("SET_GIMBAL_ANGLE_TASK");
        out.writeFloat(angle);
    }

    void start(){
        // Point camera down at an angle
        DJISDKManager.getInstance().getProduct().getGimbal().rotate(rotation, (error) -> {
            if(error != null)
                listener.statusUpdate(TaskState.FAILED, title + ": " + error.getDescription());
            else
                listener.statusUpdate(TaskState.COMPLETED, String.format("Rotated Gimbal to %.1f", angle));
        });
    }

    void stop(){

    }
}
