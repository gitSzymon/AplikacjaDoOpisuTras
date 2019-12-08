package com.example.aplikacjadoopisutras;

import androidx.appcompat.app.AppCompatActivity;
import logic.Description;
import logic.Route;

import android.content.Intent;
import android.os.Bundle;
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

        Description description = new Description(1, txtMessage.getText().toString());
       // Toast.makeText(getApplicationContext(), description.toString(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

        Route.addPoint(description);
        startActivity(intent);
    }
}
