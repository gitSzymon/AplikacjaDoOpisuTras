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

public class PointListActivity extends AppCompatActivity implements PointListAdapter.ItemClickListener{

    PointListAdapter adapter;
    ArrayList<String> pointDescriptions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_list);

        // data to populate the RecyclerView with
        class GetDescription extends AsyncTask<Void, Void, List<Description>> {

            @Override
            protected List<Description> doInBackground(Void... voids) {
                //odczytanie danych z bazy
                List<Description> descriptionsList = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .userDao()
                        .getDescriptions();
                return descriptionsList;
            }

            @Override
            protected void onPostExecute(List<Description> descriptionList) {
                //wpisanie danych z bazy do stringa i do UI
                super.onPostExecute(descriptionList);
                for(int i=0; i<descriptionList.size(); i++){
                  //  animalNames.add(descriptionList.get(i).toString());
                    pointDescriptions.add(descriptionList.get(i).getDescription());

                }
                // set up the RecyclerView
                RecyclerView recyclerView = findViewById(R.id.pointList);
                recyclerView.setLayoutManager(new LinearLayoutManager(PointListActivity.this));
                adapter = new PointListAdapter(PointListActivity.this, pointDescriptions);
                adapter.setClickListener(PointListActivity.this);
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