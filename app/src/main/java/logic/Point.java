package logic;

import java.util.Date;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

public abstract class Point {
    @PrimaryKey(autoGenerate = true)
    // @ColumnInfo(name = "PointId")
            int pointId;    //identyfikator punktu
    @ColumnInfo(name = "Date")
    Date date;
    @ColumnInfo(name = "routeId")
    int routeId;
    @ColumnInfo(name = "gpsX")
    double gpsX;    //współrzędna X lokalizacji
    @ColumnInfo(name = "gpsY")
    double gpsY;    //współrzędna Y lokalizacji

    public Point(double gpsX, double gpsY) {
        long timestamp = System.currentTimeMillis();
        date = new Date(timestamp);
        this.gpsX = gpsX;
        this.gpsY = gpsY;
    }

    //  public int getPointId() {
    //      return pointId;
    //  }

    public double getGpsX() {
        return gpsX;
    }

    public double getGpsY() {
        return gpsY;
    }

    @Override
    public String toString() {
        return "Point{" +
                "pointId=" + pointId +
                ", date=" + date +
                ", gpsX=" + gpsX +
                ", gpsY=" + gpsY +
                ", routeID" + routeId +
                '}';
    }
}

