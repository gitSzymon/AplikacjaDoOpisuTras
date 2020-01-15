package com.example.aplikacjadoopisutras;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

public class LocationService extends Service {
    private double gpsX;
    private double gpsY;
    private final IBinder binder = new MyBinder();

    public double getGpsX() {
        return gpsX;
    }

    public double getGpsY() {
        return gpsY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LocationManager managerLocation = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener listenerLocation = new LocationListener() {


            @Override
            public void onLocationChanged(Location location) {
                gpsX = location.getLatitude();
                gpsY = location.getLongitude();
             //   Toast.makeText(getApplicationContext(), "gpsX: " + gpsX + " gpsY: " + gpsY, Toast.LENGTH_SHORT).show();
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

        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            return;
        }
        managerLocation.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listenerLocation);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class MyBinder extends Binder{
        LocationService getLocationService(){
            return LocationService.this;
        }
    }

}
