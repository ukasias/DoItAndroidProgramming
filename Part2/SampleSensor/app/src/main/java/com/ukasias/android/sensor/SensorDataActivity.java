package com.ukasias.android.sensor;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

public class SensorDataActivity extends AppCompatActivity implements SensorEventListener {
    final static String TAG = "SensorDataActivity";

    TextView nameText;
    TextView accuracyText;
    TextView valuesText;

    int sensorIndex;
    String sensorName;

    SensorManager manager;
    List<Sensor> sensors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_data);

        nameText = findViewById(R.id.name);
        accuracyText = findViewById(R.id.accuracy);
        valuesText = findViewById(R.id.values);

        manager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
        sensors = manager.getSensorList(Sensor.TYPE_ALL);

        Intent passedIntent = getIntent();
        if (passedIntent != null) {
            sensorIndex =
                    passedIntent.getIntExtra(SensorDataActivity.SENSOR_SERVICE, 0);
            sensorName = sensors.get(sensorIndex).getName();
            nameText.setText(sensorName);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        manager.registerListener(
                this,
                sensors.get(sensorIndex),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onStop() {
        super.onStop();

        manager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        String data = "Sensor Timestamp : " + sensorEvent.timestamp + "\n\n";

        for (int index = 0; index < sensorEvent.values.length; ++index) {
            data += ("Sensor Value #" + (index + 1) + " : " + sensorEvent.values[index] + "\n");
        }
        valuesText.setText(data);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        accuracyText.setText("Sensor Accuracy : " + getSensorAccuracyAsString(i));
    }

	private String getSensorAccuracyAsString(int accuracy) {
		String accuracyString = "";

		switch(accuracy) {
			case SensorManager.SENSOR_STATUS_ACCURACY_HIGH: accuracyString = "High"; break;
			case SensorManager.SENSOR_STATUS_ACCURACY_LOW: accuracyString = "Low"; break;
			case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM: accuracyString = "Medium"; break;
			case SensorManager.SENSOR_STATUS_UNRELIABLE: accuracyString = "Unreliable"; break;
			default: accuracyString = "Unknown";

			break;
		}

		return accuracyString;
	}

    
}
