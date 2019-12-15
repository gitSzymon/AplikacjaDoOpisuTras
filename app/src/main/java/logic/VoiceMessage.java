package logic;

public class VoiceMessage extends Point {

    String fileName;

    public VoiceMessage(double gpsX, double gpsY, String fileName) {
        super(gpsX, gpsY);
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "VoiceMessage{" +
                "fileName='" + fileName + '\'' +
                ", pointId=" + pointId +
               // ", date=" + date +
                '}';
    }


}
