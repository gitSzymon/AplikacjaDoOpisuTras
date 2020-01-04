package com.example.aplikacjadoopisutras;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import logic.*;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
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

    public Description description1;
    public Photo photo1;
    public VoiceMessage voiceMessage1;
    public static double gpsX, gpsY;

    String currentImagePath = null;
    private static final int IMAGE_REQUEST = 1; //camera intent
    File imageFile = null;

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
        addPhoto = (Button) findViewById(R.id.btnAddPhoto);

        LocationManager managerLocation = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener listenerLocation = new LocationListener() {


            @Override
            public void onLocationChanged(Location location) {
                gpsX = location.getLatitude();
                gpsY = location.getLongitude();
                textGpsX.setText("x: " + location.getLatitude());                               //zaktualizowanie współrzędnych na ekranie
                textGpsY.setText("y: " + location.getLongitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }


        };


        //if((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) &&
        //        (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)){
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        managerLocation.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listenerLocation);


        // Get View reference
        // mText = (TextView) findViewById(R.id.text);

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

    public void onClickBtnAddText(View view) {
        Intent intent = new Intent(getApplicationContext(), AddMessage.class);
        startActivity(intent);
    }

    public void onClickBtnAddVoiceText(View view) {

        Intent intent = new Intent(getApplicationContext(), PointListActivity.class);
        startActivity(intent);

    }

    public void onClickBtnAddPhoto(View view) {

      /*      addPhoto.setText("Usuń pierwszy element z bazy");       //tymczasowa funkcja klawisza

            class DeleteDescription extends AsyncTask<Void, Void, Void> {

                @Override
                protected Void doInBackground(Void... voids) {
                    Description description =
                            DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().userDao().getDescriptions().get(0);        //pierwszy punkt listy
                    DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().userDao().delete(description);     //kasowanie ostatniego punktu

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);        //powrót do MainActivity
                    startActivity(intent);

                    return null;
                }

            }

            DeleteDescription dd = new DeleteDescription();
            dd.execute();

       */

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            try {
                imageFile = getImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (imageFile != null) {
                Uri imageUri = FileProvider.getUriForFile(this, "android.support.v4.content.FileProvider", imageFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(cameraIntent, IMAGE_REQUEST);
            }
        }
    }

    //metoda odpowiedzialna za nazwę pliku do zapisu
    private File getImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMDD_HHmmss").format(new Date());
        String imageName = "jpg_" + timeStamp + "_";
        File storageDir = getExternalFilesDir((Environment.DIRECTORY_PICTURES));
        File imageFile = File.createTempFile(imageName, ".jpg", storageDir);
        currentImagePath = imageFile.getAbsolutePath();
        return imageFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // po powrotach z activity np. aparat
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST) { //powrót z aparatu
            if (resultCode == RESULT_OK) {
                //utworzenie obiektu photo i zapisanie do bazy
                Photo photo = new Photo(MainActivity.gpsX, MainActivity.gpsY, imageFile.getName());
                //zapis do Bazy w inny wątku
                //  DatabaseClient.SavePhotoToDB save = new DatabaseClient.SavePhotoToDB();
             //   DatabaseClient.savePhotoToDB.setPhoto(photo);
             //   DatabaseClient.savePhotoToDB.execute();

                Toast.makeText(getApplicationContext(), "Sukces", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Anulowano", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public int getCurrentRouteId() {
        return currentRouteId;
    }

    public void setCurrentRouteId(int currentRouteId) {
        this.currentRouteId = currentRouteId;
    }
}
