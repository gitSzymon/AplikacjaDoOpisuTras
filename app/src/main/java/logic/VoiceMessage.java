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


@Entity(tableName = "voiceMessages")
public class VoiceMessage extends Point {

    @ColumnInfo(name = "fileName")
    String fileName;

    @Dao
    public interface VoiceMessageDao{
        @Query("SELECT * FROM voiceMessages")
        List<VoiceMessage> getVoiceMessages();

        @Insert(onConflict = OnConflictStrategy.IGNORE)
        long insert(VoiceMessage voiceMessage);

        @Update
        void update(VoiceMessage voiceMessage);

        @Delete()
        void delete(VoiceMessage voiceMessage);

    }


    public VoiceMessage(double gpsX, double gpsY, String fileName) {
        super(gpsX, gpsY);
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "VoiceMessage{" +
                "fileName='" + fileName + '\'' +
                ", pointId=" + pointId +
                ", date=" + date +
                '}';
    }


}
