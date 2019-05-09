package com.ukasias.android.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    final static String TAG = "Mission-25";

    final static int PIC_1 = R.drawable.smile_01;
    final static int PIC_2 = R.drawable.smile_02;

    SensorManager manager;
    List<Sensor> sensor;

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        imageView.setImageResource(PIC_1);

        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (manager != null) {
            sensor = manager.getSensorList(Sensor.TYPE_PROXIMITY);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        manager.registerListener(
                this,
                sensor.get(0),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onStop() {
        super.onStop();

        manager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float distance = sensorEvent.values[0];

        if (distance <= 0) {
            imageView.setImageResource(PIC_2);
        }
        else {
            imageView.setImageResource(PIC_1);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
