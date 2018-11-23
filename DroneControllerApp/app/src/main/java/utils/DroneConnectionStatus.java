package utils;

public enum DroneConnectionStatus {
    DRONE_CONNECTED("Connected"),
    DRONE_DISCONNECTED("Disconnected"),
    DRONE_CONNECTION_ERROR("Error");

    String status;
    DroneConnectionStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
