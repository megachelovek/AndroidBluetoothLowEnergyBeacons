package com.ssau.danilius.diplomablebeacons.data;


import java.io.Serializable;

public class BluetoothBeaconOnMapEntity implements Serializable {
    private String macAddress;
    private String name;
    private int currentX;
    private int currentY;
    private int lastRSSI;

    public BluetoothBeaconOnMapEntity(String macAddress, String name1, int lastRSSI, int currentX1, int currentY1) {
        this.macAddress = macAddress;
        this.name = name1;
        this.currentX = currentX1;
        this.currentY = currentY1;
        this.lastRSSI = lastRSSI;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCurrentX() {
        return currentX;
    }

    public void setCurrentX(int currentX) {
        this.currentX = currentX;
    }

    public int getCurrentY() {
        return currentY;
    }

    public void setCurrentY(int currentY) {
        this.currentY = currentY;
    }

    public int getLastRSSI() {
        return lastRSSI;
    }

    public void setLastRSSI(int lastRSSI) {
        this.lastRSSI = lastRSSI;
    }
}
