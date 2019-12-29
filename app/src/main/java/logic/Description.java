package logic;

import java.util.List;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.ForeignKey;
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

    }

    public Description(double gpsX, double gpsY, String description) {
        super(gpsX, gpsY);
        this.description = description;
    }

    public String getDescription() {
        return description;
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
