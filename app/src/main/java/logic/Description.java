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

@Entity(tableName = "descriptions")
public class Description extends Point {

    @ColumnInfo(name = "description")
    String description;

    @Dao
    public interface DescriptionDao{
        @Query("SELECT * FROM descriptions")
        List<Description> getDescriptions();

        @Insert(onConflict = OnConflictStrategy.IGNORE)
        long insert(Description description);

        @Update
        void update(Description description);

        @Delete()
        void delete(Description description);

        //kasowanie z bazy wiadomości tekstowej z danym routeId
        @Query("DELETE FROM descriptions WHERE routeId=:routeId")
        void delete(int routeId);

        @Query("SELECT COUNT(*) FROM descriptions WHERE routeId=:routeId")
        int count(int routeId);

    }

    public Description(double gpsX, double gpsY, String description, int routeId) {
        super(gpsX, gpsY);
        this.description = description;
        this.routeId = routeId;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Description{" +
                "description='" + description + '\'' +
                ", pointId=" + pointId +
                ", date=" + date +
                ", gpsX=" + gpsX +
                ", gpsY=" + gpsY +
                ". routeId=" + routeId+
                '}';
    }
}
