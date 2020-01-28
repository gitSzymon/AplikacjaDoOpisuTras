package com.example.aplikacjadoopisutras;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import logic.DatabaseClient;
import logic.Description;
import logic.Route;

public class SearchRouteActivity extends AppCompatActivity implements PointListAdapter.ItemClickListener{

    PointListAdapter adapter;
    ArrayList<Route> routeNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_route);

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
                return routeList;
            }

            @Override
            protected void onPostExecute(List<Route> routeList) {
                //wpisanie danych z bazy do stringa i do UI
                super.onPostExecute(routeList);
                for(int i=0; i<routeList.size(); i++){
                    routeNames.add(routeList.get(i));
                }
                // set up the RecyclerView
                RecyclerView recyclerView = findViewById(R.id.pointList);
                recyclerView.setLayoutManager(new LinearLayoutManager(SearchRouteActivity.this));
                adapter = new PointListAdapter(SearchRouteActivity.this, routeNames);
                adapter.setClickListener(SearchRouteActivity.this);
                recyclerView.setAdapter(adapter);
            }
        }

        GetDescription gd = new GetDescription();
        gd.execute();


    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }

}