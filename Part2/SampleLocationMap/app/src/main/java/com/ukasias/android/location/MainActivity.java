package com.ukasias.android.location;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    Button button;
    SupportMapFragment mapFragment;
    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.mapFragment);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.d(TAG, "Google Map is ready");

                map = googleMap;
            }
        });

        try {
            MapsInitializer.initialize(this);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestMyLocation();
            }
        });
    }

    private void requestMyLocation() {
        LocationManager manager =
                (LocationManager) getSystemService(LOCATION_SERVICE);
        long minTime = 1000;
        float minDistance = 0.0f;

        Location val = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (val != null) {
            toast("request: (" + val.getLatitude() + ", " + val.getLongitude());
        }
        else {
            toast("request: null");
        }


        manager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                minTime,
                minDistance,
                new GPSListener());
    }

    class GPSListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                showCurrentLocation(
                        location.getLatitude(),
                        location.getLongitude()
                );
                toast(location.getLatitude() + ", " + location.getLongitude());
            }
            else {
                toast("onLocationChanged() - null");
            }
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

        private void showCurrentLocation(Double latitude, Double longitude) {
            LatLng curPoint = new LatLng(latitude, longitude);

            map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));
        }
    }

    private void toast(String message) {
        Toast.makeText(
                getApplicationContext(),
                message,
                Toast.LENGTH_LONG).show();
    }
}