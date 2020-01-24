package com.example.aplikacjadoopisutras;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import logic.DatabaseClient;
import logic.VoiceMessage;

public class VoiceMessageActivity extends AppCompatActivity {

    private static final int VOICE_REQUEST = 2; //voice recorder intent
    String currentImagePath = null;
    File voiceRecordFile = null;

    private LocationService locationService;
    MediaRecorder recorder = null;
    String fileName = "";
    final String LOG_TAG = "AudioRecordTest";

    private Button btnStart;
    private Button btnStop;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_message);

        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);

        btnStart.setEnabled(true);
        btnStop.setEnabled(false);

        fileName = getExternalFilesDir("").getAbsolutePath();

        try {
            fileName = getVoiceRecordFile().getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        Intent intent = new Intent(this, LocationService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void onClickButtonStart(View view) {

        recorder.start();
        Toast.makeText(getApplicationContext(), getString(R.string.start_recording), Toast.LENGTH_SHORT).show();
        btnStart.setEnabled(false);
        btnStop.setEnabled(true);
    }

    public void onClickButtonStop(View view) {

        recorder.stop();
        recorder.reset();
        recorder.release();
        recorder = null;

        Long timeStamp = System.currentTimeMillis();
        Date date = new Date(timeStamp);

        VoiceMessage voiceMessage = new VoiceMessage(locationService.getGpsX(), locationService.getGpsY(), fileName, MapsActivity.currentRouteId);
        voiceMessage.setDate(date);

        //zapis do Bazy w inny wątku
        DatabaseClient.getInstance(getApplicationContext()).savePointToDb(voiceMessage);
        Toast.makeText(getApplicationContext(), getString(R.string.finish_recording), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(intent);
    }


    //metoda odpowiedzialna za nazwę pliku do zapisu
    private File getVoiceRecordFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMDD_HHmmss").format(new Date());
        String voiceMessageName = "mp3_" + timeStamp + "_";
        File storageDir = getExternalFilesDir((Environment.DIRECTORY_MUSIC));
        File voiceMessageFile = File.createTempFile(voiceMessageName, ".mp3", storageDir);
        // currentImagePath = voiceMessageFile.getAbsolutePath();
        return voiceMessageFile;
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