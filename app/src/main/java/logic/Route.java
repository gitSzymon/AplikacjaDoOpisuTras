package logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Update;


@Entity(tableName = "routes")
public class Route {

    @PrimaryKey(autoGenerate = true)
    private int routeId;
    private static ArrayList<Point> pointArrayList = new ArrayList<>();       //Arraylista miejsc
    private static ArrayList<Double> track = new ArrayList<>();                //ślad trasy (na razie tylko współrzędnych X)
    @ColumnInfo(name = "routeName")
    String routeName;
    @ColumnInfo(name = "date")
    Date date;


    public Route(String routeName) {
        this.routeName = routeName;
    }

    @Dao
    public interface RouteDao {
        @Query("SELECT * FROM routes")
        List<Route> getRoutes();

        @Query("SELECT routeId FROM routes WHERE routeName=:name")
        int findRouteIdByName(String name);

        @Query("SELECT COUNT (*) FROM routes")
        int getRoutesCount();

        @Query("SELECT routeId FROM routes")
        List<Integer> getRouteIdFromDb();

        @Insert(onConflict = OnConflictStrategy.IGNORE)
        long insert(Route route);

        @Update
        void update(Route route);

        @Delete()
        void delete(Route route);

    }

    public String getRouteName() {
        return routeName;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public void setRouteName(String routeName) {
        routeName = routeName;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public static ArrayList<Point> getPointArrayList() {
        return pointArrayList;
    }

    @Override
    public String toString() {
        String tmp;
        tmp = "Route{";
        for (int i = 0; i < pointArrayList.size(); i++) {
            tmp += pointArrayList.toString();
        }
        //             "placesArrayList=" + placesArrayList +
        //             ", track=" + track +
        //             ", routeName='" + routeName + '\'' +
        //             '}';
        return tmp;
    }

    public static void addPoint(Point point) {
        pointArrayList.add(point);
    }

    public static void removePoint(Point point) {
        pointArrayList.remove(point);
    }

    public static void addGpsPoint(Double gpsPoint) {
        track.add(gpsPoint);
    }


}