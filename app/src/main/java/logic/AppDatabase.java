package logic;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Description.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

        private static Database INSTANCE;
        public abstract Description.DescriptionDao userDao();

}