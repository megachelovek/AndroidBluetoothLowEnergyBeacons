package com.ssau.danilius.diplomablebeacons.data;

import android.provider.BaseColumns;

public final class BeaconContract {
    private BeaconContract() {    };

    public static final class BeaconEntry implements BaseColumns {
        public final static String TABLE_NAME = "beaconmap";

        public final static String COLUMN_ID = "id";
        public final static String COLUMN_MAC = "mac";
        public final static String COLUMN_NAME = "name";
        public final static String COLUMN_LASR_RSSI = "last_rssi";
        public final static String COLUMN_BEACON_MAP_X = "beacon_map_x";
        public final static String COLUMN_BEACON_MAP_Y = "beacon_map_y";

    }
}
