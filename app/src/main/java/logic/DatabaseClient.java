package logic;

import android.content.Context;
import android.os.AsyncTask;

import com.example.aplikacjadoopisutras.MainActivity;

import androidx.room.Room;

public class DatabaseClient {
    private Context mCtx;
    private static DatabaseClient mInstance;
    private AppDatabase appDatabase;                //obiekt bazy danych

    private DatabaseClient(Context mCtx) {
        this.mCtx = mCtx;

        //creating the app database with Room database builder
        //MyToDos is the name of the database
        appDatabase = Room.databaseBuilder(mCtx, AppDatabase.class, "MyToDos").build();
    }

    public static synchronized DatabaseClient getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new DatabaseClient(mCtx);
        }
        return mInstance;
    }

    public void savePointToDb(Point point){
        AddPointAsync addPoint = new AddPointAsync();
        addPoint.execute(point);
    }

    class AddPointAsync extends AsyncTask<Point, Void, Void>{
        @Override
        protected Void doInBackground(Point... points) {
            if(points[0] instanceof Description){
                Description description = (Description) points[0];
                DatabaseClient.getInstance(mCtx).getAppDatabase().userDao().insert(description);
            }
            if(points[0] instanceof Photo){
                Photo photo = (Photo) points[0];
                DatabaseClient.getInstance(mCtx).getAppDatabase().photoDao().insert(photo);
            }
            if(points[0] instanceof VoiceMessage){
                VoiceMessage voiceMessage = (VoiceMessage) points[0];
                DatabaseClient.getInstance(mCtx).getAppDatabase().voiceMessageDao().insert(voiceMessage);
            }
            return null;
        }
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}
