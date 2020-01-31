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

        //kasowanie z bazy wiadomości głosowej z danym routeId
        @Query("DELETE FROM voiceMessages WHERE routeId=:routeId")
        void delete(int routeId);


        @Query("SELECT COUNT(*) FROM voiceMessages WHERE routeId=:routeId")
        int count(int routeId);
    }


    public VoiceMessage(double gpsX, double gpsY, String fileName, int routeId) {
        super(gpsX, gpsY);
        this.fileName = fileName;
        this.routeId = routeId;
    }

    public String getFileName() {
        return fileName;
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
