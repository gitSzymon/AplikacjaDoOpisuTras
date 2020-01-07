package com.example.aplikacjadoopisutras;


import androidx.fragment.app.FragmentActivity;
import logic.DatabaseClient;
import logic.Description;
import logic.Photo;
import logic.Point;
import logic.VoiceMessage;

import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

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

                tmp.addAll(photoList);
                tmp.addAll(descriptionsList);
                tmp.addAll(voiceMessageList);


                return tmp;
            }


            @Override
            protected void onPostExecute(ArrayList<Point> pointList) {

                LatLng point = null;
                float zoomLevel = (float)20.0;
                super.onPostExecute(pointList);
                // Add a markers
                for (int i = 0; i < pointList.size(); i++) {
                    point = new LatLng(pointList.get(i).getGpsX(), pointList.get(i).getGpsY());
                    MarkerOptions marker = new MarkerOptions();

                    if (pointList.get(i) instanceof Description) {
                        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                        mMap.addMarker(marker.position(point).title(((Description) pointList.get(i)).getDescription()));
                    }
                    if (pointList.get(i) instanceof Photo) {
                        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        mMap.addMarker(marker.position(point).title("Fotosek " + i));
                    }
                    if (pointList.get(i) instanceof VoiceMessage) {
                        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                        mMap.addMarker(marker.position(point).title("Głosówka " + i));
                    }

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, zoomLevel));
            }
        }

        GetMapPoints getPoint = new GetMapPoints();
        getPoint.execute();
    }
}


