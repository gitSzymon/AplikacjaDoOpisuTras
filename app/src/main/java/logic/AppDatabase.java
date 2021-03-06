package logic;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Description.class, Route.class, Photo.class, VoiceMessage.class, LocationPoint.class}, version = 1)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {

        private static Database INSTANCE;
        public abstract Description.DescriptionDao userDao();
        public abstract Route.RouteDao routeDao();
        public abstract Photo.PhotoDao photoDao();
        public abstract VoiceMessage.VoiceMessageDao voiceMessageDao();
        public abstract LocationPoint.LocationDao locationDao();

}