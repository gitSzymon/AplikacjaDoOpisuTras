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
    private LocationService locationService;
    public static double gpsX, gpsY;
    String currentImagePath = null;
    private static final int IMAGE_REQUEST = 1; //camera intent
    int MY_PERMISSIONS_RECORD_AUDIO = 3;
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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_RECORD_AUDIO);
        }

        // Get View reference
        // mText = (TextView) findViewById(R.id.text);

        Intent intent = new Intent(this, LocationService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
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

        //Intent intent = new Intent(getApplicationContext(), PointListActivity.class);
        //startActivity(intent);

        Intent intent = new Intent(getApplicationContext(), VoiceMessageActivity.class);
        startActivity(intent);

        /*
        Intent voiceRecorderIntent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        if (voiceRecorderIntent.resolveActivity(getPackageManager()) != null) {
            try {
                voiceRecordFile = getVoiceRecordFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (voiceRecordFile != null) {
                Uri imageUri = FileProvider.getUriForFile(this, "android.support.v4.content.FileProvider", voiceRecordFile);
                voiceRecorderIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(voiceRecorderIntent, VOICE_REQUEST);
            }
        }

*/


    }

    public void onClickBtnAddPhoto(View view) {

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

    public void onClickBtnUpdate(View view) {

        textGpsX.setText(Double.toString(locationService.getGpsX()));
        textGpsY.setText(Double.toString(locationService.getGpsY()));

    }

    public void onClickBtnStartRecordingRoute(View view) {
        locationService.setRecording(true);
    }

    public void onClickBtnStopRecordingRoute(View view) {
        locationService.setRecording(false);
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
                Photo photo = new Photo(locationService.getGpsX(), locationService.getGpsY(), currentImagePath, 1);
                //zapis do Bazy w inny wątku
                DatabaseClient.getInstance(getApplicationContext()).savePointToDb(photo);

                Toast.makeText(getApplicationContext(), "Sukces", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Anulowano", Toast.LENGTH_SHORT).show();
            }
        }

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

    public int getCurrentRouteId() {
        return currentRouteId;
    }

    public void setCurrentRouteId(int currentRouteId) {
        this.currentRouteId = currentRouteId;
    }


}
