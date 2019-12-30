package com.example.aplikacjadoopisutras;

import androidx.appcompat.app.AppCompatActivity;
import logic.DatabaseClient;
import logic.Description;
import logic.Point;
import logic.Route;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;


public class AddMessage extends AppCompatActivity {

    private TextView txtMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_voice_message);


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

        class SaveDescription extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                int tmpRouteId = MainActivity.currentRouteId;
                Description description = new Description(MainActivity.gpsX, MainActivity.gpsY ,txtMessage.getText().toString().trim(), tmpRouteId); //utworzenie obiektu
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().userDao().insert(description);     //dodanie punktu do bazy

                // Intent intent = new Intent(getApplicationContext(), OneDescriptionActivity.class);        //powrót do MainActivity
                // intent.putExtra("id", 10);
                // startActivity(intent);


                Intent intent = new Intent(getApplicationContext(), MainActivity.class);        //powrót do MainActivity
                startActivity(intent);

                return null;
            }

        }

        SaveDescription sd = new SaveDescription();
        sd.execute();
    }
}
