package com.ssau.danilius.diplomablebeacons.data;


        import com.google.gson.Gson;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class RequestBeaconDatabase {
    public static void GETALL(DataOutputStream dos) throws IOException, InterruptedException {
        dos.writeUTF("GETALL");
        dos.flush();
    }
    public static void ADD(DataOutputStream dos, BluetoothBeaconOnMapEntity beacon) throws IOException, InterruptedException {
        dos.writeUTF("ADD "+beacon.getName()+" "+beacon.getMacAddress()+" "+beacon.getLastRSSI()+" "+beacon.getCurrentX()+" "+beacon.getCurrentY());
        dos.flush();
    }
    public static void ADDLIST(DataOutputStream dos, List<BluetoothBeaconOnMapEntity> beaconList) throws IOException, InterruptedException {
        Gson gson = new Gson();
        String listGson = gson.toJson(beaconList);
        dos.writeUTF("ADDLIST "+listGson);
        dos.flush();
    }
    public static void DELETE(DataOutputStream dos, BluetoothBeaconOnMapEntity beacon) throws IOException, InterruptedException {
        dos.writeUTF("DELETE "+beacon.getName()+" "+beacon.getMacAddress()+" "+beacon.getLastRSSI()+" "+beacon.getCurrentX()+" "+beacon.getCurrentY());
        dos.flush();
    }
    public static void EDIT(DataOutputStream dos,BluetoothBeaconOnMapEntity beacon,BluetoothBeaconOnMapEntity newBeacon) throws IOException, InterruptedException {
        dos.writeUTF("EDIT "+beacon.getName()+" "+beacon.getMacAddress()+" "+beacon.getLastRSSI()+" "+beacon.getCurrentX()+" "+beacon.getCurrentY()+newBeacon.getName()+" "+newBeacon.getMacAddress()+" "+newBeacon.getLastRSSI()+" "+newBeacon.getCurrentX()+" "+newBeacon.getCurrentY());
        dos.flush();
    }
}
