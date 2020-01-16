package com.example.aplikacjadoopisutras;


import androidx.fragment.app.FragmentActivity;
import logic.DatabaseClient;
import logic.Description;
import logic.LocationPoint;
import logic.Photo;
import logic.Point;
import logic.VoiceMessage;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final String LOG_TAG = "MapsActivity";

    private GoogleMap mMap;
    private Marker marker;
    private MediaPlayer player = null;


    @Override
    public boolean onMarkerClick(Marker marker) {
        Point p = (Point) marker.getTag();
        String string = "";
        if (p instanceof Description) {
            string = ((Description) p).getDescription();
        }
        if (p instanceof Photo) {
            Intent intent = new Intent(getApplicationContext(), ImageViewActivity.class);
            intent.putExtra("fileName", (Photo) p);
            startActivity(intent);

        }
        if (p instanceof VoiceMessage) { //odtworzenie dzwięku
            player = new MediaPlayer();
            String fileName = ((VoiceMessage) p).getFileName();
            try {
                player.setDataSource(fileName);
                player.prepare();
                player.start();
            } catch (IOException e) {
                Log.e(LOG_TAG, "prepare() failed");
            }
            string = "Voice: gpsx=" + ((VoiceMessage) p).getGpsX() + " gpsy= " + ((VoiceMessage) p).getGpsY();
        }


        Toast.makeText(this, string, Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setOnMarkerClickListener(this);

        class GetMapPoints extends AsyncTask<Void, Void, ArrayList<Point>> {

            @Override
            protected ArrayList<Point> doInBackground(Void... voids) {
                ArrayList<Point> tmp = new ArrayList<>();

                //odczytanie danych z bazy
                List<Description> descriptionsList = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .userDao()
                        .getDescriptions();

                List<Photo> photoList = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .photoDao()
                        .getPhoto();

                List<VoiceMessage> voiceMessageList = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .voiceMessageDao()
                        .getVoiceMessages();

                List<LocationPoint> locationList = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .locationDao()
                        .getLocation();

                tmp.addAll(photoList);
                tmp.addAll(descriptionsList);
                tmp.addAll(voiceMessageList);
                tmp.addAll(locationList);


                return tmp;
            }


            @Override
            protected void onPostExecute(ArrayList<Point> pointList) {

                LatLng point = null;
                Polyline line;
                PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
                float zoomLevel = (float) 15.0;
                super.onPostExecute(pointList);
                // Add a markers
                for (int i = 0; i < pointList.size(); i++) {
                    point = new LatLng(pointList.get(i).getGpsX(), pointList.get(i).getGpsY());
                    MarkerOptions markerOpt = new MarkerOptions();
                    Point p = pointList.get(i);

                    if (pointList.get(i) instanceof Description) {
                        markerOpt.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                        markerOpt.position(point).title(((Description) pointList.get(i)).getDescription());
                        marker = mMap.addMarker(markerOpt);
                        marker.setTag(p);
                    }
                    if (pointList.get(i) instanceof Photo) {
                        markerOpt.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        markerOpt.position(point).title("Fotosek " + i);
                        marker = mMap.addMarker(markerOpt);
                        marker.setTag(p);
                    }
                    if (pointList.get(i) instanceof VoiceMessage) {
                        markerOpt.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        markerOpt.position(point).title("Głosówka " + i);
                        marker = mMap.addMarker(markerOpt);
                        marker.setTag(p);
                    }
                    if (pointList.get(i) instanceof LocationPoint) {
                        options.add(point);
                    }

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
                }

                line = mMap.addPolyline(options);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, zoomLevel)); //Nie działa na starym LG
            }
        }

        GetMapPoints getPoint = new GetMapPoints();
        getPoint.execute();

    }
}