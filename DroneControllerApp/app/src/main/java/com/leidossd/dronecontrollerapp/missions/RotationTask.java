package com.leidossd.dronecontrollerapp.missions;

import com.leidossd.djiwrapper.FlightControllerWrapper;

public class RotationTask extends Task {
    private float angle;

    RotationTask(float angle){
        this.angle = angle;
    }

    @Override
    void exec(){
        FlightControllerWrapper.getInstance().rotateTo(angle);
    }

    @Override
    String getDescription(){
        return "Rotate to " + Float.toString(this.angle);
    }

}
