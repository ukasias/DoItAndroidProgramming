package com.ukasias.android.samplelocation;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    final static String TAG = "SampleLocation";

    Button button;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        text = findViewById(R.id.textView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLocation();
            }
        });

        updateLocation();
    }

    private void checkLocation() {
        LocationManager manager =
                (LocationManager) getSystemService(LOCATION_SERVICE);

        try {
            Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            String msg = "Latitude: " + location.getLatitude()
                    + "\n Longitude: " + location.getLongitude();

            text.setText(msg);

            msg = "Altitude: " + location.getAltitude()
                    + ", Latitude: " + location.getLatitude()
                    + ", Longitude: " + location.getLongitude();
            toast(msg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateLocation() {
        GPSListener listener = new GPSListener();
        long minTime = 10000;
        float minDistance = 0;

        LocationManager manager =
                (LocationManager) getSystemService(LOCATION_SERVICE);

        manager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                minTime,
                minDistance,
                listener);
    }

    class GPSListener implements LocationListener {
        public GPSListener() {
            super();
        }

        @Override
        public void onLocationChanged(Location location) {
            String msg = "Latitude: " + location.getLatitude()
                    + "\n Longitude: " + location.getLongitude();

            text.setText(msg);

            msg = "Altitude: " + location.getAltitude()
                    + ", Latitude: " + location.getLatitude()
                    + ", Longitude: " + location.getLongitude();
            toast(msg);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }

    private void toast(String message) {
        Toast.makeText(
                getApplicationContext(),
                message,
                Toast.LENGTH_LONG).show();
    }
}
