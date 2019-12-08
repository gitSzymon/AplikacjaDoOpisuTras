package logic;

import java.util.Date;

public abstract class Point {
    int pointId;    //identyfikator punktu
    Date date;
    double gpsX;    //współrzędna X lokalizacji
    double gpsY;    //współrzędna Y lokalizacji

    public Point(int pointId) {
        this.pointId = pointId;
        long timestamp = System.currentTimeMillis();
        date = new Date(timestamp);
    }

    @Override
    public String toString() {
        return "Point{" +
                "pointId=" + pointId +
                ", date=" + date +
                ", gpsX=" + gpsX +
                ", gpsY=" + gpsY +
                '}';
    }
}

