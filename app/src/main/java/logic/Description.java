package logic;

import java.util.List;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

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
    }

    public Description(double gpsX, double gpsY, String description) {
        super(gpsX, gpsY);
        this.description = description;
    }

    @Override
    public String toString() {
        return "Description{" +
                "description='" + description + '\'' +
                ", pointId=" + pointId +
               // ", date=" + date +
                ", gpsX=" + gpsX +
                ", gpsY=" + gpsY +
                '}';
    }
}
