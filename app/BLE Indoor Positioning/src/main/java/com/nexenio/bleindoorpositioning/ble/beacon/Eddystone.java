package com.nexenio.bleindoorpositioning.ble.beacon;

import com.nexenio.bleindoorpositioning.ble.advertising.EddystoneAdvertisingPacket;
import com.nexenio.bleindoorpositioning.location.provider.BeaconLocationProvider;
import com.nexenio.bleindoorpositioning.location.provider.EddystoneLocationProvider;

import java.util.UUID;

/**
 * Created by steppschuh on 15.11.17.
 */

public class Eddystone<P extends EddystoneAdvertisingPacket> extends Beacon<P> {

    public static final int CALIBRATION_DISTANCE_DEFAULT = 0;
    protected UUID proximityUuid;
    protected int major;
    protected int minor;

    public Eddystone() {
        this.calibratedDistance = CALIBRATION_DISTANCE_DEFAULT;
    }

    @Override
    public BeaconLocationProvider<Eddystone<P>> createLocationProvider() {
        return new EddystoneLocationProvider<Eddystone<P>>(this) {
            @Override
            protected void updateLocation() {
                // nope
            }

            @Override
            protected boolean canUpdateLocation() {
                return false;
            }
        };
    }

    @Override
    public String toString() {
        return "IBeacon{" +
                "proximityUuid=" + proximityUuid +
                ", major=" + major +
                ", minor=" + minor +
                ", macAddress='" + macAddress + '\'' +
                ", rssi=" + rssi +
                ", calibratedRssi=" + calibratedRssi +
                ", transmissionPower=" + transmissionPower +
                ", advertisingPackets=" + advertisingPackets +
                '}';
    }

    public UUID getProximityUuid() {
        return proximityUuid;
    }

    public void setProximityUuid(UUID proximityUuid) {
        this.proximityUuid = proximityUuid;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

}
