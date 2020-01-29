package com.example.aplikacjadoopisutras;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import logic.DatabaseClient;
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
            route.setDate(new Date(System.currentTimeMillis()));
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
