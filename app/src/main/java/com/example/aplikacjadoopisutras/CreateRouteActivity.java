package com.example.aplikacjadoopisutras;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import logic.DatabaseClient;
import logic.Description;
import logic.Route;

public class CreateRouteActivity extends AppCompatActivity {

    private TextView routeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_route);

        routeName = (TextView) findViewById(R.id.routeName);
    }


    public void onClickBtnStartRoute(View view) {
        //utworzenie nowej trasy w bazie
        if (routeName.getText().toString().trim().isEmpty()) {
            routeName.setError("Nie wpisałeś nazwy trasy!!!");
            routeName.requestFocus();
            return;
        }

        String name = routeName.getText().toString().trim();
       // MapsActivity.currentRouteId = tmpId;
        MapsActivity.currentRouteName = name;


        SaveDescription rd = new SaveDescription();
        rd.execute();

        setResult(RESULT_OK);
        finish();
    }

    class SaveDescription extends AsyncTask<Void, Void, Void> {
        int tmpId;
        @Override
        protected Void doInBackground(Void... voids) {

            String name = routeName.getText().toString().trim();
            Route route = new Route(name); //utworzenie obiektu
            DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().routeDao().insert(route);     //dodanie trasy do bazy
            tmpId = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().routeDao().findRouteIdByName(name);    //odczytanie id, ktore nadala baza danych
            MapsActivity.currentRouteId = tmpId;
            return null;
        }
    }

    public void onClickBtnCancel(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

}
