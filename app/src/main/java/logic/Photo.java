package logic;

public class Photo extends Point {

    String fileName;


    public Photo(double gpsX, double gpsY, String fileName) {
        super(gpsX, gpsY);
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "fileName='" + fileName + '\'' +
                ", pointId=" + pointId +
                //", date=" + date +
                '}';
    }


}
