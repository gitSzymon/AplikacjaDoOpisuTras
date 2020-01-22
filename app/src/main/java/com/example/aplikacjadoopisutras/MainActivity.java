package com.example.aplikacjadoopisutras;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import logic.*;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private TextView textMain;
    private TextView textGpsX, textGpsY;
    private Button addPhoto;
    public static int currentRouteId;

    public static double gpsX, gpsY;

    private static final int IMAGE_REQUEST = 1; //camera intent
    int MY_PERMISSIONS_RECORD_AUDIO = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent().getExtras() != null) {
            currentRouteId = getIntent().getExtras().getInt("routeId");
        }
        //GPS//
        textMain = (TextView) findViewById(R.id.gps);
        textGpsX = (TextView) findViewById(R.id.textGpsX);
        textGpsY = (TextView) findViewById(R.id.textGpsY);
        textMain.setMovementMethod(new ScrollingMovementMethod());          //ustawienie scrollowania


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_RECORD_AUDIO);
        }

        // Get View reference
        // mText = (TextView) findViewById(R.id.text);

        Intent intent = new Intent(this, LocationService.class);
   //     bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void onClickBtnPrintRoute(View view) {

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
                StringBuilder tmp = new StringBuilder();
                super.onPostExecute(descriptionList);
                for (int i = 0; i < descriptionList.size(); i++) {
                    tmp.append(descriptionList.get(i).toString());
                }
                textMain.setText(tmp.toString());
            }
        }

        GetDescription gd = new GetDescription();
        gd.execute();
    }
}
