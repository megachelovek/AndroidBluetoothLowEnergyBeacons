package com.ssau.danilius.diplomablebeacons.data;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.nexenio.bleindoorpositioning.ble.beacon.signal.MeanFilter;
import com.ssau.danilius.diplomablebeacons.R;

import java.util.List;

public class BeaconAdapter extends BaseAdapter {
    private Context mContext;
    private List<BluetoothBeaconOnMapEntity> mListBeacons;
    private List<String> mListSimple;
    Intent chooseFile;
    MeanFilter filter;

    public BeaconAdapter(Context context, List<BluetoothBeaconOnMapEntity> list) {
        mContext = context;
        mListBeacons = list;
        mListSimple=null;
        filter = new MeanFilter(1000);
    }

    @Override
    public int getCount() {
        return mListBeacons.size();
    }

    @Override
    public Object getItem(int pos) {
        return mListBeacons.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {

        BluetoothBeaconOnMapEntity entry = mListBeacons.get(pos);


        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.list_item_layout, null);
        }


        TextView tvName = (TextView) convertView.findViewById(R.id.macAddress);
        tvName.setText(entry.getMacAddress());


        TextView tvMajor = (TextView) convertView.findViewById(R.id.rssi);
        float rssi = entry.getLastRSSI();
        if (rssi != 0 && !Float.isNaN(rssi)){
            tvMajor.setText(String.valueOf(entry.getLastRSSI()));
        }


        TextView tvMinor = (TextView) convertView.findViewById(R.id.distance);
        if (entry.getName()!= ""){
            tvMinor.setText(entry.getName());
        }

        return convertView;
    }

    public void updateData(List<BluetoothBeaconOnMapEntity> data) {
        this.mListBeacons = data;
        notifyDataSetChanged();
    }
}