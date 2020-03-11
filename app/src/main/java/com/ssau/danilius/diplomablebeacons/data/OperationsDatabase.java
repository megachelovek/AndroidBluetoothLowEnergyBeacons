package com.ssau.danilius.diplomablebeacons.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OperationsDatabase{
    private static SQLiteDatabase db;
    private static BeaconMapDbHelper dbHelper;
    private static Cursor c;
    private static Context context;
    private static List<BluetoothBeaconOnMapEntity> listBeaconFromDB;

    public OperationsDatabase(Context context){
        this.context = context;
        listBeaconFromDB = GetBeaconsFromDatabase();
    }

    public void setContext(Context context) {
        OperationsDatabase.context = context;
    }

    public List<BluetoothBeaconOnMapEntity> GetBeaconsFromDatabase(){
        List<BluetoothBeaconOnMapEntity> listBeacon = new ArrayList<BluetoothBeaconOnMapEntity>();
        dbHelper = new BeaconMapDbHelper(context);
        try {
            dbHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }
        try {
            db = dbHelper.getReadableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }
        c = db.rawQuery("SELECT * FROM beaconmap;", null);
        if (c.moveToFirst()) {
            do {
                listBeacon.add(new BluetoothBeaconOnMapEntity(c.getString(1), c.getString(2), c.getInt(3),c.getInt(4),c.getInt(5)));
            }
            while (c.moveToNext());
        }
        c.close();
        return listBeacon;
    }

    public boolean DeleteBeaconsFromDatabase(BluetoothBeaconOnMapEntity beacon){
        try{
            db.delete("beaconmap", "mac = ?", new String[]{beacon.getMacAddress()});
            return true;
        }
        catch(Exception e){
            return false;
        }
    }

    public boolean UpdateOrCreateBeaconsFromDatabase(BluetoothBeaconOnMapEntity beacon){
        dbHelper = new BeaconMapDbHelper(context);
        try {
            dbHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

        try {
            db = dbHelper.getReadableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }

        ContentValues cv = new ContentValues();
        cv.put(BeaconContract.BeaconEntry.COLUMN_MAC, beacon.getMacAddress());
        cv.put(BeaconContract.BeaconEntry.COLUMN_NAME, beacon.getName());
        cv.put(BeaconContract.BeaconEntry.COLUMN_LASR_RSSI, beacon.getLastRSSI());
        cv.put(BeaconContract.BeaconEntry.COLUMN_BEACON_MAP_X, beacon.getCurrentX());
        cv.put(BeaconContract.BeaconEntry.COLUMN_BEACON_MAP_Y, beacon.getCurrentY());
        String tenp =  beacon.getMacAddress();
        try{
            if (BeaconExistInDatabase(beacon.getMacAddress())) {
                db.update("beaconmap", cv, "mac = ?", new String[]{tenp});
            } else {
                db.insertOrThrow("beaconmap", null, cv);
            }
            return true;
        }
        catch(Exception e){
            return false;
        }
    }

    private Boolean BeaconExistInDatabase(String macAdress){
        for (BluetoothBeaconOnMapEntity beaconItem:listBeaconFromDB) {
            if(macAdress.equals(beaconItem.getMacAddress())){
                return true;
            }
        }
        return false;
    }

}
