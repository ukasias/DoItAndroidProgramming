package com.ukasias.android.sensor;

import android.app.ListActivity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.util.List;

public class MainActivity extends ListActivity {
    final static String TAG = "MainActivity";

    SensorManager manager;
    List<Sensor> sensors;
    SensorListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensors = manager.getSensorList(Sensor.TYPE_ALL);

        adapter = new SensorListAdapter(this, R.layout.listitem, sensors);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Sensor sensor = sensors.get(position);
        String sensorName = sensor.getName();

        Intent intent = new Intent(this, SensorDataActivity.class);
        intent.putExtra(SensorDataActivity.SENSOR_SERVICE, position);

        startActivity(intent);
    }

    private void print(String message) {
        Log.d(TAG, message + "\n");
    }
}
