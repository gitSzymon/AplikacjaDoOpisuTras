package logic;

import java.util.ArrayList;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "routes")
public class Route {

    @PrimaryKey(autoGenerate = true)
    int routeId;
    private static ArrayList<Point> pointArrayList = new ArrayList<>();       //Arraylista miejsc
    private static ArrayList<Double> track = new ArrayList<>();                //ślad trasy (na razie tylko współrzędnych X)
    @ColumnInfo(name = "routeName")
    private static String routeName;

    public static void setRouteName(String routeName) {
        Route.routeName = routeName;
    }

    public static ArrayList<Point> getPointArrayList() {
        return pointArrayList;
    }

    @Override
    public String toString() {
        String tmp;
        tmp =   "Route{";
        for(int i=0; i<pointArrayList.size(); i++){
            tmp += pointArrayList.toString();
        }
   //             "placesArrayList=" + placesArrayList +
   //             ", track=" + track +
   //             ", routeName='" + routeName + '\'' +
   //             '}';
        return tmp;
    }

    public static void addPoint(Point point){
        pointArrayList.add(point);
    }

    public static void removePoint(Point point){
        pointArrayList.remove(point);
    }

    public static void addGpsPoint(Double gpsPoint){
        track.add(gpsPoint);
    }


}