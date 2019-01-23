package com.leidossd.utils;

import android.support.annotation.NonNull;

public enum DroneConnectionStatus {
    DRONE_CONNECTED("Connected"),
    DRONE_DISCONNECTED("Disconnected"),
    DRONE_CONNECTION_ERROR("Error");

    String status;
    DroneConnectionStatus(String status) {
        this.status = status;
    }

    @Override
    @NonNull
    public String toString() {
        return status;
    }
}
