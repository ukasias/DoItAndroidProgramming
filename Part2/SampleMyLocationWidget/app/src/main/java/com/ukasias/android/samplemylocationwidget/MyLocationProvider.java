package com.ukasias.android.samplemylocationwidget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

import java.io.IOException;
import java.util.List;

public class MyLocationProvider extends AppWidgetProvider {
    final static String TAG = "MyLocationProvider";

    public static double ycoord = 0.00;
    public static double xcoord = 0.00;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        Log.d(TAG, "onUpdated() called : " + ycoord + ", " + xcoord);

        final int size = appWidgetIds.length;

        for (int i = 0; i < size; i++) {
            int appWidgetId = appWidgetIds[i];

            String uriBegin = "geo:" + ycoord + ", " + xcoord;
            String query = ycoord + "," + xcoord + "(" + "내위치" + ")";
            String encodedQuery = Uri.encode(query);
            String uriString = uriBegin + "?q=" + encodedQuery + "&z=15";
            Uri uri = Uri.parse(uriString);

            Log.d(TAG, "uriString: " + uriString);

            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.mylocation);
            views.setOnClickPendingIntent(R.id.txtInfo, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

        context.startService(new Intent(context, GPSLocationService.class));
    }

    public static class GPSLocationService extends Service {
        public static final String TAG = "GPSLocationService";

        private LocationManager manager = null;

        private LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "onLocationChanged() called.");

                updateCoordinates(location.getLatitude(), location.getLongitude());

                stopSelf();
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
        };

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onCreate() {
            super.onCreate();

            Log.d(TAG, "onCreate() called.");

            manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        }

        @Override
        public void onDestroy() {
            Log.d(TAG, "onDestroy() called.");

            stopListening();

            super.onDestroy();
        }

        /*
        @Override
        public void onStart(Intent intent, int startId) {
            super.onStart(intent, startId);

            startListening();
        }*/

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            startListening();

            return super.onStartCommand(intent, flags, startId);
        }

        private void startListening() {
            Log.d(TAG, "startListening() called.");

            final Criteria criteria = new Criteria();

            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            criteria.setAltitudeRequired(true);
            criteria.setBearingRequired(true);
            criteria.setCostAllowed(true);
            criteria.setPowerRequirement(Criteria.POWER_LOW);

            final String bestProvider = manager.getBestProvider(criteria, true);

            if (bestProvider != null && bestProvider.length() > 0) {
                manager.requestLocationUpdates(bestProvider, 500, 10, listener);
            }
            else {
                final List<String> providers = manager.getProviders(true);

                for (final String provider : providers) {
                    manager.requestLocationUpdates(provider, 500, 10, listener);
                }
            }
        }

        private void stopListening() {
            try {
                if (manager != null && listener != null) {
                    manager.removeUpdates(listener);
                }
                manager = null;
            }
            catch(final Exception e) {

            }
        }

        private void updateCoordinates(double latitude, double longitude) {
            Geocoder coder = new Geocoder(this);
            List<Address> addresses = null;
            String info = "";

            Log.d(TAG, "updateCoordinate() called.");

            try {
                addresses = coder.getFromLocation(latitude, longitude, 2);
                if (null != addresses && addresses.size() > 0) {
                    int addressCount = addresses.get(0).getMaxAddressLineIndex();

                    if (-1 != addressCount) {
                        for (int index = 0; index <= addressCount; ++index) {
                            info += addresses.get(0).getAddressLine(index);

                            if (index < addressCount)
                                info += ", ";
                        }
                    }
                    else {
                        info += addresses.get(0).getFeatureName() + ", "
                                + addresses.get(0).getSubAdminArea() + ", "
                                + addresses.get(0).getAdminArea();
                    }
                }

                Log.d(TAG, "Address : " + addresses.get(0).toString());
            }
            catch(IOException ioe) {
                ioe.printStackTrace();
            }

            coder = null;
            addresses = null;

            xcoord = longitude;
            ycoord = latitude;

            if (info.length() <= 0) {
                info = "[내 위치] " + latitude + ", " + longitude
                        + "\n터치하면 지도로 볼 수 있습니다.";
            }
            else {
                info += ("\n" + "[내 위치]" + latitude + ", " + longitude + ")");
                info +="\n터치하면 지도로 볼 수 있습니다.";
            }

            RemoteViews views = new RemoteViews(getPackageName(), R.layout.mylocation);

            views.setTextViewText(R.id.txtInfo, info);

            ComponentName thisWidget = new ComponentName(this, MyLocationProvider.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(this);
            manager.updateAppWidget(thisWidget, views);

            Log.d(TAG, "coordinates : " + latitude + ", " + longitude);
        }
    }
}
