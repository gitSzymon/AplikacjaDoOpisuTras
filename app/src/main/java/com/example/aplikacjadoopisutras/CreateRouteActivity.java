package com.example.aplikacjadoopisutras;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

        routeName = (TextView)findViewById(R.id.routeName);

    }


    public void onClickBtnStartRoute(View view) {
        //utworzenie nowej trasy w bazie
        if (routeName.getText().toString().trim().isEmpty()) {
            routeName.setError("Nie wpisałeś nazwy trasy!!!");
            routeName.requestFocus();
            return;
        }

        class SaveDescription extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                Route route = new Route(routeName.getText().toString().trim()); //utworzenie obiektu
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().routeDao().insert(route);     //dodanie punktu do bazy

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);        //powrót do MainActivity
                startActivity(intent);

                return null;
            }

        }

        SaveDescription rd = new SaveDescription();
        rd.execute();
    }

//        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
 //       startActivity(intent);
//    }

    public void onClickBtnCancel(View view) {
           Intent intent = new Intent(getApplicationContext(), StartActivity.class);
           startActivity(intent);
    }


}
