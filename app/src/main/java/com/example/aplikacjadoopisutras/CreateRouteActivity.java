package com.example.aplikacjadoopisutras;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class CreateRouteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_route);

    }


    public void onClickBtnStartRoute(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void onClickBtnCancel(View view) {
           Intent intent = new Intent(getApplicationContext(), StartActivity.class);
           startActivity(intent);
    }


}
