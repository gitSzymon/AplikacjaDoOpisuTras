package logic;


import java.util.List;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Entity(tableName = "photos")
public class Photo extends Point {

    @ColumnInfo(name = "fileName")
    String fileName;


    @Dao
    public interface PhotoDao{
        @Query("SELECT * FROM photos")
        List<Photo> getPhoto();

        @Insert(onConflict = OnConflictStrategy.IGNORE)
        long insert(Photo photo);

        @Update
        void update(Photo photo);

        @Delete()
        void delete(Photo photo);

    }

    public Photo(double gpsX, double gpsY, String fileName, int routeId) {
        super(gpsX, gpsY);
        this.fileName = fileName;
        this.routeId = routeId;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "fileName='" + fileName + '\'' +
                ", pointId=" + pointId +
                ", date=" + date +
                '}';
    }


}
