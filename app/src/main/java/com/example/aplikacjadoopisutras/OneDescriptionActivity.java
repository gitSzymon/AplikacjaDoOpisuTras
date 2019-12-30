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

public class OneDescriptionActivity extends AppCompatActivity {

    private TextView descriptionText;
    private TextView gpsXText;
    private TextView gpsYText;
    private TextView dataText;

    private Description desc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_description);


        Button okButton = (Button)findViewById(R.id.okButton);
        Button cancelButton = (Button)findViewById(R.id.cancelButton);

        descriptionText = (TextView)findViewById((R.id.descriptionText));
        gpsXText = (TextView)findViewById((R.id.gpsXText));
        gpsYText = (TextView)findViewById((R.id.gpsYText));
        dataText = (TextView)findViewById((R.id.dataText));

        //wydobycie obiektu z bazy
     //   final int tmp = getIntent().getIntExtra("Id", 1);

     //   int id=-1;

        class GetDescription extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
       //         desc = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().userDao().getDescriptions().get(tmp);

           //     id = getIntent().getExtras().getInt("id");
              //  Intent intent = new Intent(getApplicationContext(), MainActivity.class);        //powrót do MainActivity
              //  intent.getIntExtra("id",id);
              //  startActivity(intent);

                return null;
            }

            public Description getDesc() {
                return desc;
            }
        }

    //    if(getIntent().getExtras() != null){
    //        id = getIntent().getExtras().getInt("id",-1);
    //    }
        GetDescription gd = new GetDescription();
        gd.execute();


        //wypełnienie pól tekstowych danymi obiektu desc
        descriptionText.setText(gd.getDesc().getDescription());
        gpsXText.setText(String.valueOf(gd.getDesc().getGpsX()));
        gpsYText.setText(String.valueOf(gd.getDesc().getGpsY()));

    }

    public void onClickCancelButton(View view) {

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);        //powrót do MainActivity
        startActivity(intent);
    }

    public void onClickOkButton(View view){

        if (descriptionText.getText().toString().trim().isEmpty()) {
            descriptionText.setError("Nie można dodac pustej notatki!!!");
            descriptionText.requestFocus();
            return;
        }

        class SaveDescription extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                Double gpsX = Double.parseDouble(String.valueOf(gpsXText.getText()));
                Double gpsY = Double.parseDouble(String.valueOf(gpsYText.getText()));
                Description description = new Description(gpsX, gpsY, descriptionText.getText().toString().trim(), MainActivity.currentRouteId); //utworzenie obiektu
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().userDao().update(description);     //edycja punktu

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);        //powrót do MainActivity
                startActivity(intent);

                return null;
            }

        }

        SaveDescription sd = new SaveDescription();
        sd.execute();
    }

}
