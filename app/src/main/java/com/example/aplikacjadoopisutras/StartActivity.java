package com.example.aplikacjadoopisutras;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {

    private Button btnNowaTrasa;
    private Button btnKontynuujTrase;
    private Button btnPrzegladTras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);



    }

    public void onClickBtnNowaTrasa(View view) {
        Intent intent = new Intent(getApplicationContext(), CreateRouteActivity.class);
        startActivity(intent);
    }

    public void onClickBtnKontynuujTrase(View view) {
     //   Intent intent = new Intent(getApplicationContext(), StartActivity.class);
     //   startActivity(intent);
    }

    public void onClickBtnPrzegladTras(View view) {
     //   Intent intent = new Intent(getApplicationContext(), StartActivity.class);
     //   startActivity(intent);
    }

}
