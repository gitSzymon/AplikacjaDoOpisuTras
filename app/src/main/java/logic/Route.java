package logic;

import java.util.ArrayList;

public class Route {
    private static ArrayList<Point> pointArrayList = new ArrayList<>();       //Arraylista miejsc
    private static ArrayList<Double> track = new ArrayList<>();                //ślad trasy (na razie tylko współrzędnych X)
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