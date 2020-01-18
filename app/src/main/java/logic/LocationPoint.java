package logic;

import com.example.aplikacjadoopisutras.LocationService;

import java.util.Date;
import java.util.List;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Query;
import androidx.room.Update;

@Entity(tableName = "locations")
public class LocationPoint extends Point {

    public LocationPoint(double gpsX, double gpsY, int routeId) {
        super(gpsX, gpsY);
        this.routeId = routeId;
    }

    @Dao
    public interface LocationDao {
        @Query("SELECT * FROM locations")
        List<LocationPoint> getLocation();

        @Query("SELECT * FROM locations WHERE routeId =:routeId")
        List<LocationPoint> getLocationsByRouteId(int routeId);

        @Insert(onConflict = OnConflictStrategy.IGNORE)
        long insert(LocationPoint locationPoint);

        @Update
        void update(LocationPoint locationPoint);

        @Delete()
        void delete(LocationPoint locationPoint);

    }
}
