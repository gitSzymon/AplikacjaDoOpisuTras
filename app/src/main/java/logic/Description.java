package logic;

public class Description extends Point {

    String description;

    public Description(int pointId) {
        super(pointId);
    }

    public Description(int pointId, String description) {
        super(pointId);
        this.description = description;
    }

    @Override
    public String toString() {
        return "Description{" +
                "description='" + description + '\'' +
                ", pointId=" + pointId +
                ", date=" + date +
                '}';
    }
}
