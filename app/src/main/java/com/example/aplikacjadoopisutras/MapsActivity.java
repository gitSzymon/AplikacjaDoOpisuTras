/*TODO:

3. Zapisywanie tras do pliku .kml
6. Ustawienie stringów w pliku string.xml (wyrzucenie hardcoded)
7. Debuging zdalny (biblioteka internetowa crashlytics.com)
10. Ekran ustawień (jak często zapisywac lokalizację LocationPoint)
11. Obsługa błędów przy zaniku sygnału GPS
13. Ukrywanie markerów (np. wyświetlanie samych Photo)
14. Usuwanie punktów
15. Do photo trzeba dodać nazwę trasy (w photoActivity)
16. Po anuluj na dyktafonie, zatrzymać nagranie jeśli było rozpoczęte

*/

package com.example.aplikacjadoopisutras;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import logic.DatabaseClient;
import logic.Description;
import logic.LocationPoint;
import logic.Photo;
import logic.Point;
import logic.VoiceMessage;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final String LOG_TAG = "MapsActivity";
    private static final int ROUTE_REQUEST = 1;
    private static final int IMAGE_REQUEST = 2;

    private GoogleMap mMap;
    private Marker marker;
    float zoomLevel = (float) 15.0;
    private MediaPlayer player = null;
    private static ArrayList<PolylineOptions> optionsList = new ArrayList<>();
    private static ArrayList<Integer> routesToDraw = new ArrayList<>();     //ArrayLista id tras które mają być wyświetlane
    public static int currentRouteId;
    public static String currentRouteName;
    public static TextView routeName;
    private LocationService locationService;
    private Button btnNowaTrasa;
    private Button btnStopRecording;
    private Button btnAddPhoto;
    private Button btnAddMessage;
    private Button btnAddVoiceMessage;
    String currentImagePath = null;
    File imageFile = null;


    public static void setOptionsList(PolylineOptions polylineOption) {
        optionsList.add(polylineOption);
    }

    public static ArrayList<PolylineOptions> getOptionsList() {
        return optionsList;
    }

    public static ArrayList<Integer> getRoutesToDraw() {
        return routesToDraw;
    }

    public static void setRoutesToDraw(ArrayList<Integer> routesToDraw) {
        MapsActivity.routesToDraw = routesToDraw;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Point p = (Point) marker.getTag();
        String string = "";
        if (p instanceof Description) {
            string = ((Description) p).getDescription();
        }
        if (p instanceof Photo) {
            Intent intent = new Intent(getApplicationContext(), ImageViewActivity.class);
            intent.putExtra("fileName", (Photo) p);
            startActivity(intent);

        }
        if (p instanceof VoiceMessage) { //odtworzenie dzwięku
            player = new MediaPlayer();
            String fileName = ((VoiceMessage) p).getFileName();
            try {
                player.setDataSource(fileName);
                player.prepare();
                player.start();
            } catch (IOException e) {
                Log.e(LOG_TAG, "prepare() failed");
            }
            string = "Voice: gpsx=" + ((VoiceMessage) p).getGpsX() + " gpsy= " + ((VoiceMessage) p).getGpsY();
        }

        Toast.makeText(this, string, Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, LocationService.class);
        startService(intent);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        routeName = (TextView) findViewById(R.id.routeName);
        btnNowaTrasa = (Button) findViewById(R.id.btnNowaTrasa);
        btnStopRecording = (Button) findViewById(R.id.btnStopRecordingRoute);
        btnAddPhoto = (Button) findViewById(R.id.btnAddPhoto);
        btnAddMessage = (Button) findViewById(R.id.btnAddMessage);
        btnAddVoiceMessage = (Button) findViewById(R.id.btnAddVoiceMessage);
        routeName.setText("Aktualna trasa: " + currentRouteName);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MapsActivity.routeName.setText("Aktualna trasa: " + currentRouteName);

        // po powrotach z activity np. aparat
        if (requestCode == ROUTE_REQUEST) {
            if (resultCode == RESULT_OK) {
                btnNowaTrasa.setEnabled(false);
                btnStopRecording.setEnabled(true);
                btnNowaTrasa.setVisibility(View.GONE);
                btnAddMessage.setVisibility(View.VISIBLE);
                btnAddPhoto.setVisibility(View.VISIBLE);
                btnAddVoiceMessage.setVisibility(View.VISIBLE);
                btnStopRecording.setVisibility(View.VISIBLE);

                locationService.setRecording(true);
                locationService.setRouteId(currentRouteId);
            }
        }
        if (requestCode == IMAGE_REQUEST) { //powrót z aparatu
            if (resultCode == RESULT_OK) {
                //utworzenie obiektu photo i zapisanie do bazy
                Photo photo = new Photo(locationService.getGpsX(), locationService.getGpsY(), currentImagePath, currentRouteId);
                long timestamp = System.currentTimeMillis();
                Date date = new Date(timestamp);
                photo.setDate(date);

                //zapis do Bazy w inny wątku
                DatabaseClient.getInstance(getApplicationContext()).savePointToDb(photo);

                Toast.makeText(getApplicationContext(), "Sukces", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Anulowano", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setOnMarkerClickListener(this);

        class GetMapPoints extends AsyncTask<Void, Void, ArrayList<Point>> {

            @Override
            protected ArrayList<Point> doInBackground(Void... voids) {
                ArrayList<Point> tmp = new ArrayList<>();

                //odczytanie danych z bazy
                List<Description> descriptionsList = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .userDao()
                        .getDescriptions();

                List<Photo> photoList = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .photoDao()
                        .getPhoto();

                List<VoiceMessage> voiceMessageList = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .voiceMessageDao()
                        .getVoiceMessages();

                List<LocationPoint> locationList = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .locationDao()
                        .getLocation();

                tmp.addAll(photoList);
                tmp.addAll(descriptionsList);
                tmp.addAll(voiceMessageList);
                tmp.addAll(locationList);

                // odczyt id wszystkich tras
                List<Integer> listRoutesId = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .routeDao()
                        .getRouteIdFromDb();

                // petla po wszystkich trasach
                for (Integer i : listRoutesId) {
                    PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
                    List<LocationPoint> lp = DatabaseClient
                            .getInstance(getApplicationContext())
                            .getAppDatabase()
                            .locationDao()
                            .getLocationsByRouteId(i);
                    //petla po wszystkich LocationPoin i-tej trasy
                    for (LocationPoint j : lp) {
                        LatLng lt = new LatLng(j.getGpsX(), j.getGpsY());
                        options.add(lt);
                    }
                    for (Integer k : routesToDraw) {
                        if (i == k) {
                            MapsActivity.setOptionsList(options);
                        }
                    }
                }
                return tmp;
            }

            @Override
            protected void onPostExecute(ArrayList<Point> pointList) {

                LatLng point = null;
                Polyline line;
                super.onPostExecute(pointList);
                // Add a markers
                for (int i = 0; i < pointList.size(); i++) {
                    point = new LatLng(pointList.get(i).getGpsX(), pointList.get(i).getGpsY());
                    MarkerOptions markerOpt = new MarkerOptions();
                    Point p = pointList.get(i);
                    for (Integer j : routesToDraw) {
                        if (p.getRouteId() == j) {
                            //rysowanie punktów jeśli routeId jest takie jak podał użytkownik
                            //(na liście routesToDraw)
                            if (pointList.get(i) instanceof Description) {
                                markerOpt.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                                markerOpt.position(point).title("Id trasy: " + pointList.get(i).getRouteId() + "  " + pointList.get(i).getDate().toString() + ((Description) (pointList.get(i))).getDescription());
                                marker = mMap.addMarker(markerOpt);
                                marker.setTag(p);
                            }
                            if (pointList.get(i) instanceof Photo) {
                                markerOpt.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                markerOpt.position(point).title("Zdjęcie nr: " + i + "  Id trasy: " + pointList.get(i).getRouteId());
                                marker = mMap.addMarker(markerOpt);
                                marker.setTag(p);
                            }
                            if (pointList.get(i) instanceof VoiceMessage) {
                                markerOpt.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                markerOpt.position(point).title("Wiadomość głosowa nr: " + i + "  Id trasy: " + pointList.get(i).getRouteId());
                                marker = mMap.addMarker(markerOpt);
                                marker.setTag(p);
                            }
                        }
                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
                }

                //rysowanie punktów lokalizacyjnych
                for (PolylineOptions i : MapsActivity.getOptionsList()) {
                    mMap.addPolyline(i);
                }

                if (point == null) {
                    point = new LatLng(51.757, 19.458);
                    zoomLevel = 10;
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, zoomLevel)); //Nie działa na starym LG
            }
        }
        GetMapPoints getPoint = new GetMapPoints();
        getPoint.execute();
    }

    public void onClickBtnPlus(View view) {
        LatLng center = mMap.getProjection().getVisibleRegion().latLngBounds.getCenter();
        zoomLevel++;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, zoomLevel));
    }

    public void onClickBtnMinus(View view) {
        LatLng center = mMap.getProjection().getVisibleRegion().latLngBounds.getCenter();
        zoomLevel--;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, zoomLevel));
    }

    public void onClickBtnNowaTrasa(View view) {
        Intent intent = new Intent(getApplicationContext(), CreateRouteActivity.class);
        startActivityForResult(intent, ROUTE_REQUEST);
    }

    public void onClickBtnStopRecording(View view) {
        btnStopRecording.setEnabled(false);
        btnNowaTrasa.setEnabled(true);
        locationService.setRecording(false);
        btnNowaTrasa.setVisibility(View.VISIBLE);
        btnAddMessage.setVisibility(View.GONE);
        btnAddPhoto.setVisibility(View.GONE);
        btnAddVoiceMessage.setVisibility(View.GONE);
        btnStopRecording.setVisibility(View.GONE);
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

    public void onClickBtnAddVoiceMessage(View view) {
        Intent intent = new Intent(getApplicationContext(), VoiceMessageActivity.class);
        startActivity(intent);
    }

    public void onClickBtnAddMessage(View view) {
        Intent intent = new Intent(getApplicationContext(), AddMessageActivity.class);
        startActivity(intent);
    }

    public void onClickBtnChooseRoutes(View view) {
        Intent intent = new Intent(getApplicationContext(), SearchRouteActivity.class);
        startActivity(intent);
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

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LocationService.MyBinder binder = (LocationService.MyBinder) iBinder;
            locationService = binder.getLocationService();

            if (locationService == null || locationService.isRecording() == false) {
                btnNowaTrasa.setVisibility(View.VISIBLE);
                btnStopRecording.setVisibility(View.GONE);
                btnAddMessage.setVisibility(View.GONE);
                btnAddPhoto.setVisibility(View.GONE);
                btnAddVoiceMessage.setVisibility(View.GONE);
            } else {
                btnNowaTrasa.setVisibility(View.GONE);
                btnStopRecording.setVisibility(View.VISIBLE);
                btnAddMessage.setVisibility(View.VISIBLE);
                btnAddPhoto.setVisibility(View.VISIBLE);
                btnAddVoiceMessage.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            locationService = null;
        }
    };
}