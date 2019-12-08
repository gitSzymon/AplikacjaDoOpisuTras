package com.example.aplikacjadoopisutras;

import androidx.appcompat.app.AppCompatActivity;
import logic.*;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;


public class MainActivity extends AppCompatActivity {

    private TextView textMain;
    private TextView textGpsX, textGpsY;

    public Description description1;
    public Photo photo1;
    public VoiceMessage voiceMessage1;
    public double gpsX, gpsY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //GPS//
        textMain = (TextView) findViewById(R.id.gps);
        textGpsX = (TextView) findViewById(R.id.textGpsX);
        textGpsY = (TextView) findViewById(R.id.textGpsY);

        LocationManager managerLocation = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener listenerLocation = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                gpsX = location.getLatitude();
                gpsY = location.getLongitude();
                textGpsX.setText("x: " + location.getLatitude());                               //zaktualizowanie współrzędnych na ekranie
                textGpsY.setText("y: " + location.getLongitude());
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


        //if((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) &&
        //        (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)){
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        managerLocation.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listenerLocation);


        // Get View reference
        // mText = (TextView) findViewById(R.id.text);



        Route.setRouteName("trasa 1");
    }

    public void onClickBtnPrintRoute(View view) {
        StringBuilder tmp = new StringBuilder();
        for(int i=0; i<Route.getPointArrayList().size(); i++){
            tmp.append(Route.getPointArrayList().get(i).toString());
        }
        textMain.setText(tmp.toString());
    }

    public void onClickBtnAddText(View view) {
        Intent intent = new Intent(getApplicationContext(), AddMessage.class);
        startActivity(intent);
    }

    public void onClickBtnAddVoiceText(View view) {

    }

    public void onClickBtnAddPhoto(View view) {

    }

}