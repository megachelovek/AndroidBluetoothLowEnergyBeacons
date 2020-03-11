package com.ssau.danilius.diplomablebeacons;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ssau.danilius.diplomablebeacons.data.BeaconMapDbHelper;
import com.ssau.danilius.diplomablebeacons.data.BluetoothBeaconOnMapEntity;
import com.ssau.danilius.diplomablebeacons.data.OperationsDatabase;

import java.util.ArrayList;
import java.util.List;

public class EditBeaconActivity extends AppCompatActivity {

    private TextView beaconMacField,beaconNameField,beaconRSSIField,beaconMapXField,beaconMapYField;
    private String name, macAdress;
    private int beacon_map_x,beacon_map_y,rssi;
    private Button btnSave,btnDelete;
    private SQLiteDatabase db;
    private BeaconMapDbHelper dbHelper;
    private List<BluetoothBeaconOnMapEntity> listBeaconFromDB = new ArrayList<BluetoothBeaconOnMapEntity>();
    BluetoothBeaconOnMapEntity beacon;
    OperationsDatabase operationsDatabase;
    Cursor c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_beacon);

        //Button oclBtnSave = findViewById(R.id.button_save);
        TextView textView = new TextView(this);
        textView.setTextSize(20);
        textView.setPadding(16, 16, 16, 16);
        btnSave = (Button) findViewById(R.id.button_save);
        btnDelete = (Button) findViewById(R.id.button_delete);
        beaconMacField = (EditText) findViewById(R.id.beacon_mac);
        beaconNameField = (EditText) findViewById(R.id.beacon_name);
        beaconRSSIField = (EditText) findViewById(R.id.current_rssi);
        beaconMapXField = (EditText) findViewById(R.id.current_map_x);
        beaconMapYField = (EditText) findViewById(R.id.current_map_y);
        operationsDatabase = new OperationsDatabase(this);

        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            listBeaconFromDB = (List<BluetoothBeaconOnMapEntity>) arguments.getSerializable("ListBeaconDatabase");
            BluetoothBeaconOnMapEntity beacon = (BluetoothBeaconOnMapEntity) arguments.getSerializable("Beacon");
            beaconMacField.setText(beacon.getMacAddress());
            beaconNameField.setText(beacon.getName());
            beaconRSSIField.setText(Integer.toString(beacon.getLastRSSI()));
            beaconMapXField.setText(Integer.toString(beacon.getCurrentX()));
            beaconMapYField.setText(Integer.toString(beacon.getCurrentY()));
        }

        //Сохранить
        View.OnClickListener oclBtnSave = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = beaconNameField.getText().toString();
                macAdress = beaconMacField.getText().toString();
                rssi = Integer.valueOf(beaconMapYField.getText().toString());
                beacon_map_x = Integer.valueOf(beaconMapXField.getText().toString());
                beacon_map_y = Integer.valueOf(beaconMapYField.getText().toString());

                beacon = new BluetoothBeaconOnMapEntity(macAdress,name,rssi,beacon_map_x,beacon_map_y);
                operationsDatabase.UpdateOrCreateBeaconsFromDatabase(beacon);

                Intent intent = new Intent(EditBeaconActivity.this, MainActivity.class);
                startActivity(intent);
            }
        };
        btnSave.setOnClickListener(oclBtnSave);

        //Удалить
        View.OnClickListener oclBtnDelete = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operationsDatabase.DeleteBeaconsFromDatabase(beacon);
                Intent intent = new Intent(EditBeaconActivity.this, MainActivity.class);
                startActivity(intent);
            }
        };
        btnDelete.setOnClickListener(oclBtnDelete);
    }
}
