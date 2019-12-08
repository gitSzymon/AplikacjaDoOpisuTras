package logic;

public class Photo extends Point {

    String fileName;

    public Photo(int pointId, String fileName) {
        super(pointId);
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "fileName='" + fileName + '\'' +
                ", pointId=" + pointId +
                ", date=" + date +
                '}';
    }


}
