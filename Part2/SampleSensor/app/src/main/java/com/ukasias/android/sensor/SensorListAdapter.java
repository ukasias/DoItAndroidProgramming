package com.ukasias.android.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class SensorListAdapter extends BaseAdapter {
    final static String TAG = "SensorListAdapter";

    private Context context;
    private int listItemLayout;
    private List<Sensor> list;

    SensorListAdapter(Context context, int listItemLayout, List<Sensor> list) {
        super();
        this.context = context;
        this.listItemLayout = listItemLayout;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list != null? list.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return list != null? list.get(i) : null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }



    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = new View(context);

            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(listItemLayout, null);
        }

        Sensor sensor = null;
        if (list != null) {
            sensor = list.get(i);
        }
        if (sensor != null) {
            TextView name = view.findViewById(R.id.nameText);
            TextView manufacturer = view.findViewById(R.id.manufacturerText);
            TextView version = view.findViewById(R.id.versionText);

            name.setText("센서: " + sensor.getName());
            manufacturer.setText("제조사: " + sensor.getVendor());
            version.setText("버전: " + sensor.getVersion());
        }

        return view;
    }

    private void print(String message) {
        Log.d(TAG, message + "\n");
    }
}
