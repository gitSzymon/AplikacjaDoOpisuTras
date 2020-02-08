package com.example.aplikacjadoopisutras;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import logic.DatabaseClient;
import logic.Description;
import logic.LocationPoint;
import logic.Photo;
import logic.Route;
import logic.VoiceMessage;

public class SearchRouteActivity extends AppCompatActivity implements PointListAdapter.ItemClickListener {

    PointListAdapter adapter;
    ArrayList<Route> routeNames = new ArrayList<>();
    public static CheckBox checkBoxSelectAll;
    public static DataBase db = new DataBase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_route);
        checkBoxSelectAll = findViewById(R.id.checkBoxMarkAll);

        // data to populate the RecyclerView with
        class GetDescription extends AsyncTask<Void, Void, List<Route>> {

            @Override
            protected List<Route> doInBackground(Void... voids) {
                //odczytanie danych z bazy
                List<Route> routeList = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .routeDao()
                        .getRoutes();
                db.setRoutes((ArrayList) routeList);
                //descriptions
                List<Description> desc = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .userDao()
                        .getDescriptions();
                db.setDescriptions((ArrayList) desc);
                //photo
                List<Photo> photo = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .photoDao()
                        .getPhoto();
                db.setPhotos((ArrayList) photo);
                //voice
                List<VoiceMessage> voiceMessages = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .voiceMessageDao()
                        .getVoiceMessages();
                db.setVoices((ArrayList) voiceMessages);
                //locations
                List<LocationPoint> locationPoint = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .locationDao()
                        .getLocation();
                db.setLocations((ArrayList) locationPoint);
                return routeList;
            }

            @Override
            protected void onPostExecute(List<Route> routeList) {
                //wpisanie danych z bazy do stringa i do UI
                super.onPostExecute(routeList);
                for (int i = 0; i < routeList.size(); i++) {
                    routeNames.add(routeList.get(i));
                }
                // set up the RecyclerView
                RecyclerView recyclerView = findViewById(R.id.pointList);
                recyclerView.setLayoutManager(new LinearLayoutManager(SearchRouteActivity.this));
                adapter = new PointListAdapter(SearchRouteActivity.this, routeNames);
                adapter.setClickListener(SearchRouteActivity.this);
                recyclerView.setAdapter(adapter);

                if(routeNames.size() == MapsActivity.getRoutesToDraw().size()){     // jeśli ilość tras w bazie jest taka sama jak ilość tras do narysowania
                    checkBoxSelectAll.setChecked(true);
                    adapter.selectAll(true);
                }
                else{
                    checkBoxSelectAll.setChecked(false);
                    adapter.selectAll(false);
                }
            }
        }

        GetDescription gd = new GetDescription();
        gd.execute();


    }

    public void onClickBtnOk(View view) {
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(intent);
    }

    public void onClickBtnMarked(View view) {
        MapsActivity.getRoutesToDraw().clear();
        if (checkBoxSelectAll.isChecked()) {
            for (Route r : routeNames) {
                MapsActivity.getRoutesToDraw().add(r.getRouteId());
            }
            adapter.selectAll(true);
            }
        else{
            MapsActivity.getRoutesToDraw().clear();
            adapter.selectAll(false);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }

    public static class DataBase {


        ArrayList<Route> routes = new ArrayList<>();
        ArrayList<Description> descriptions = new ArrayList<>();
        ArrayList<Photo> photos = new ArrayList<>();
        ArrayList<VoiceMessage> voices = new ArrayList<>();
        ArrayList<LocationPoint> locations = new ArrayList<>();

        public DataBase() {
        }

        public ArrayList<Route> getRoutes() {
            return routes;
        }

        public void setRoutes(ArrayList<Route> routes) {
            this.routes = routes;
        }

        public ArrayList<Description> getDescriptions() {
            return descriptions;
        }

        public void setDescriptions(ArrayList<Description> descriptions) {
            this.descriptions = descriptions;
        }

        public ArrayList<Photo> getPhotos() {
            return photos;
        }

        public void setPhotos(ArrayList<Photo> photos) {
            this.photos = photos;
        }

        public ArrayList<VoiceMessage> getVoices() {
            return voices;
        }

        public void setVoices(ArrayList<VoiceMessage> voices) {
            this.voices = voices;
        }

        public ArrayList<LocationPoint> getLocations() {
            return locations;
        }

        public void setLocations(ArrayList<LocationPoint> locations) {
            this.locations = locations;
        }
    }

}