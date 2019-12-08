package logic;

public class VoiceMessage extends Point {

    String fileName;

    public VoiceMessage(int pointId, String fileName) {
        super(pointId);
        this.fileName = fileName;
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
