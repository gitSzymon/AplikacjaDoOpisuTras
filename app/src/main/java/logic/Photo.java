package logic;


import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Entity(tableName = "photos")
public class Photo extends Point implements Parcelable {

    @ColumnInfo(name = "fileName")
    String fileName;


    @Dao
    public interface PhotoDao {
        @Query("SELECT * FROM photos")
        List<Photo> getPhoto();

        @Insert(onConflict = OnConflictStrategy.IGNORE)
        long insert(Photo photo);

        @Update
        void update(Photo photo);

        @Delete()
        void delete(Photo photo);

    }

    public Photo(double gpsX, double gpsY, String fileName, int routeId) {
        super(gpsX, gpsY);
        this.fileName = fileName;
        this.routeId = routeId;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "fileName='" + fileName + '\'' +
                ", pointId=" + pointId +
                ", date=" + date +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(pointId);
        dest.writeInt(routeId);
        dest.writeDouble(gpsX);
        dest.writeDouble(gpsY);
        dest.writeString(date.toString());
        dest.writeString(fileName);
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {

        // Ta metoda odczytuje naszą klasę z obiektu typu Parcel.
        // Najpierw odczytujemy poziom pokemona, potem imię a na końcu tworzymy go z powrotem z tych danych.
        @Override
        public Photo createFromParcel(Parcel in) {
            int pointId = in.readInt();
            int routeId = in.readInt();
            double gpsX = in.readDouble();
            double gpsY = in.readDouble();
            String stringDate = in.readString();
            String fileName = in.readString();
            try {
                Date date = new SimpleDateFormat("dd/MM/yyyy").parse(stringDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }


            return new Photo(gpsX, gpsY, fileName, routeId);
        }

        // Ta metoda prealokuje tablicę dla elementów naszej klasy w przypadku, gdybyśmy wysyłali więcej niż jeden obiekt.
        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };
}