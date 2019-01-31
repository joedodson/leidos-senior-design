package com.leidossd.utils;

public enum IntentAction {
    REGISTRATION_RESULT("Registration Result", "registrationResult"),
    CONNECTION_CHANGE("Connection Change", "connectionStatus");

    String action;
    String resultKey;

    IntentAction(String action, String resultKey) {
        this.action = action;
        this.resultKey = resultKey;
    }

    public String getActionString() {
        return action;
    }

    public String getResultKey() {
        return resultKey;
    }
}
