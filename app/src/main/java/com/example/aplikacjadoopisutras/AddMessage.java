package com.example.aplikacjadoopisutras;

import androidx.appcompat.app.AppCompatActivity;
import logic.DatabaseClient;
import logic.Description;
import logic.Point;
import logic.Route;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;


public class AddMessage extends AppCompatActivity {

    private TextView txtMessage;
    private LocationService locationService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_voice_message);

        Intent intent = new Intent(this, LocationService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        Button btnOk = (Button)findViewById(R.id.btnAddText);
        txtMessage = (TextView)findViewById((R.id.txtMessage));
 /*       btnOk.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "tost", Toast.LENGTH_SHORT).show();
                //Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                //startActivity();
            }
        });
*/
    }

    public void onClickBtnCancel(View view){

        if (txtMessage.getText().toString().trim().isEmpty()) {
            txtMessage.setError("Nie dodałeś żadnej notatki!!!");
            txtMessage.requestFocus();
            return;
        }

        int tmpRouteId = MapsActivity.currentRouteId;
        Description description = new Description(locationService.getGpsX(), locationService.getGpsY() ,txtMessage.getText().toString().trim(), tmpRouteId); //utworzenie obiektu
        DatabaseClient.getInstance(getApplicationContext()).savePointToDb(description);     //dodanie punktu do bazy

        Intent intent = new Intent (getApplicationContext(), MapsActivity.class);
        startActivity(intent);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LocationService.MyBinder binder = (LocationService.MyBinder) iBinder;
            locationService = binder.getLocationService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            locationService = null;
        }
    };
}
