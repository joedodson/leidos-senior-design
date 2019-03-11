package com.leidossd.utils;

public enum Direction {
    NW("North-West"), N("North"),
    NE("North-East"), W("West"),
    E("East"), SW("South-West"),
    S("South"), SE("South-East");

    private final String dir;

    Direction(String dir) { this.dir = dir; }

    public String getDir() { return dir; }
}
