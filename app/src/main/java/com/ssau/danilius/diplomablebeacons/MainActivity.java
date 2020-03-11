package com.ssau.danilius.diplomablebeacons;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.nexenio.bleindoorpositioning.ble.advertising.IndoorPositioningAdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.ble.beacon.BeaconManager;
import com.nexenio.bleindoorpositioning.ble.beacon.BeaconUpdateListener;
import com.nexenio.bleindoorpositioning.ble.beacon.filter.BeaconFilter;
import com.nexenio.bleindoorpositioning.ble.beacon.filter.GenericBeaconFilter;
import com.nexenio.bleindoorpositioning.ble.beacon.filter.IBeaconFilter;
import com.nexenio.bleindoorpositioning.location.LocationListener;
import com.ssau.danilius.diplomablebeacons.data.AndroidLocationProvider;
import com.ssau.danilius.diplomablebeacons.data.BeaconAdapter;
import com.ssau.danilius.diplomablebeacons.data.BeaconMapDbHelper;
import com.ssau.danilius.diplomablebeacons.data.BluetoothBeaconOnMapEntity;
import com.ssau.danilius.diplomablebeacons.data.OperationsDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_UPDATE_LIST = 11;
    private ArrayList<BluetoothBeaconOnMapEntity> beaconList = new ArrayList<BluetoothBeaconOnMapEntity>();
    private ArrayList<BluetoothBeaconOnMapEntity> listBeaconFromDB = new ArrayList<BluetoothBeaconOnMapEntity>();
    private BeaconManager beaconManager = BeaconManager.getInstance();
    private static final String TAG = MainActivity.class.getSimpleName();
    private CoordinatorLayout coordinatorLayout;
    private List<String> mainListMAC = new ArrayList<String>();
    private ListView lvBeaconView;
    private BeaconAdapter beaconAdapter;
    private SQLiteDatabase db;
    private BeaconMapDbHelper dbHelper;
    OperationsDatabase operationsDatabase;
    Cursor c;

    protected List<BeaconFilter> beaconFilters = new ArrayList<>();
    protected BeaconUpdateListener beaconUpdateListener;
    protected LocationListener deviceLocationListener;
    protected IBeaconFilter uuidFilter = new IBeaconFilter(IndoorPositioningAdvertisingPacket.INDOOR_POSITIONING_UUID, UUID.fromString("acfd065e-c3c0-11e3-9bbe-1a514932ac01"));

    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        lvBeaconView = (ListView) findViewById(R.id.listBeaconsView);
        bottomNavigation = findViewById(R.id.bottom_navigation);

        beaconUpdateListener= createBeaconUpdateListener();
        beaconFilters.add(uuidFilter);
        beaconAdapter = new BeaconAdapter(this, beaconList);
        lvBeaconView.setAdapter(beaconAdapter);
        operationsDatabase = new OperationsDatabase(this);
        listBeaconFromDB = (ArrayList<BluetoothBeaconOnMapEntity>) operationsDatabase.GetBeaconsFromDatabase();

        // НАЖАТИЕ НА ЭЛЕМЕНТ СПИСКА
        lvBeaconView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                BluetoothBeaconOnMapEntity selectedItem = beaconList.get(position);
                Intent intent = new Intent(MainActivity.this, EditBeaconActivity.class);
                intent.putExtra("Beacon",selectedItem);
                intent.putExtra("ListBeaconDatabase",listBeaconFromDB);
                //intent.putExtra(Beacon.class.getSimpleName(), selectedItem);
                startActivity(intent);
            }
        });

        // setup location
        AndroidLocationProvider.initialize(this);

        // setup bluetooth
        BluetoothClient.initialize(this);
        BeaconManager.registerBeaconUpdateListener(beaconUpdateListener);

        BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.navigation_main:
                                break;
                            case R.id.navigation_map:
                                Intent mapIntent = new Intent(MainActivity.this, MapActivity.class);
                                startActivity(mapIntent);
                                break;
                            case R.id.navigation_settings:
                                Intent settingIntent = new Intent(MainActivity.this, SettingsActivity.class);
                                startActivity(settingIntent);
                                break;
                        }
                        return false;
                    }
                };
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // observe location
        if (!AndroidLocationProvider.hasLocationPermission(this)) {
            AndroidLocationProvider.requestLocationPermission(this);
        } else if (!AndroidLocationProvider.isLocationEnabled(this)) {
            requestLocationServices();
        }

        AndroidLocationProvider.startRequestingLocationUpdates();
        AndroidLocationProvider.requestLastKnownLocation();

        // observe bluetooth
        if (!BluetoothClient.isBluetoothEnabled()) {
            requestBluetooth();
        }
        BluetoothClient.startScanning();
    }

    @Override
    protected void onPause() {
        // stop observing location
        AndroidLocationProvider.stopRequestingLocationUpdates();

        // stop observing bluetooth
        BluetoothClient.stopScanning();

        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case AndroidLocationProvider.REQUEST_CODE_LOCATION_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Location permission granted");
                    AndroidLocationProvider.startRequestingLocationUpdates();
                } else {
                    Log.d(TAG, "Location permission not granted. Wut?");
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case BluetoothClient.REQUEST_CODE_ENABLE_BLUETOOTH: {
                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "Bluetooth enabled, starting to scan");
                    BluetoothClient.startScanning();
                } else {
                    Log.d(TAG, "Bluetooth not enabled, invoking new request");
                    BluetoothClient.requestBluetoothEnabling(this);
                }
                break;
            }
            case REQUEST_CODE_UPDATE_LIST:{
                beaconAdapter.notifyDataSetChanged();
            }
        }
    }

    private void requestLocationServices() {
        Snackbar snackbar = Snackbar.make(
                coordinatorLayout,
                R.string.error_location_disabled,
                Snackbar.LENGTH_INDEFINITE
        );
        snackbar.setAction(R.string.action_enable, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AndroidLocationProvider.requestLocationEnabling(MainActivity.this);
            }
        });
        snackbar.show();
    }

    private void requestBluetooth() {
                BluetoothClient.requestBluetoothEnabling(MainActivity.this);
    }

    public List<Beacon> getBeacons() {
        if (beaconFilters.isEmpty()) {

            return new ArrayList<Beacon>(beaconManager.getBeaconMap().values());
        }
        List<Beacon> beacons = new ArrayList<>();
        for (Beacon beacon : beaconManager.getBeaconMap().values()) {
            for (BeaconFilter beaconFilter : beaconFilters) {
                    beacons.add((Beacon) beacon);
                }
            }
        return beacons;
    }

    public GenericBeaconFilter createClosestBeaconFilter() {
        return new GenericBeaconFilter() {

            @Override
            public boolean matches(Beacon beacon) {
                if (BeaconManager.getInstance().getClosestBeacon().equals(beacon)) {
                    return true;
                }
                return false;
            }
        };
    }

    protected String GetBeaconNameFromDB(Beacon beacon){
        return GetNameFromBatabase(beacon.getMacAddress());
    }

    protected BeaconUpdateListener createBeaconUpdateListener() {
        return new BeaconUpdateListener() {
            @Override
            public void onBeaconUpdated(Beacon beacon) {
                if (beaconList.size()==0){
                    mainListMAC.add(beacon.getMacAddress());
                    beaconList.add(0,new BluetoothBeaconOnMapEntity(beacon.getMacAddress(),GetBeaconNameFromDB(beacon),1,2, beacon.getRssi()));
                }
                else {
                    String temp = beacon.getMacAddress();
                    if(!mainListMAC.contains(temp)) {
                        beaconList.add(0,new BluetoothBeaconOnMapEntity(beacon.getMacAddress(),GetBeaconNameFromDB(beacon),1,2, beacon.getRssi()));
                        mainListMAC.add(temp);
                    }
                }
                onActivityResult(REQUEST_CODE_UPDATE_LIST,0,null);
            }
        };
    }

    protected String GetNameFromBatabase(String macAdress){
        if(listBeaconFromDB!=null) {
            for (BluetoothBeaconOnMapEntity beaconItem : listBeaconFromDB) {
                if (macAdress.equals(beaconItem.getMacAddress())) {
                    return beaconItem.getName();
                }
            }
        }
        return "Not name in database";
    }


}
