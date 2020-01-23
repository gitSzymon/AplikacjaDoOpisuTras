package com.example.aplikacjadoopisutras;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import androidx.appcompat.app.AppCompatActivity;
import logic.Photo;
import logic.Point;

public class ImageViewActivity extends AppCompatActivity {

    private static final String TAG = "ImageViewActivity";
    private ImageView photo;
    private TextView textGpsX;
    private TextView textGpsY;
    private TextView textDate;
    private String fileName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        Log.d(TAG, "onCreate started");

        photo = (ImageView) findViewById(R.id.imageView);
        textGpsX = (TextView) findViewById(R.id.textGpsX);
        textGpsY = (TextView) findViewById(R.id.textGpsY);
        textDate = (TextView) findViewById(R.id.textDate);

        Intent intent = getIntent();
        Photo p = getIntent().getExtras().getParcelable("fileName");

        //obrazek
        Glide.with(this).asBitmap().load(p.getFileName()).into(photo);
        //teksty
        textGpsX.setText(" Gps X = " + Double.toString(p.getGpsX()));
        textGpsY.setText(" Gps Y = " + Double.toString(p.getGpsY()));
        textDate.setText(" Data: " + p.getDate().toString());
    }
}