package com.checkin.mycheckins.Models;

import java.io.Serializable;

public class CheckinModel implements Serializable {

    public static final String TABLE_NAME = "CheckIn";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "mtitle";
    public static final String COLUMN_PLACE = "mplace";
    public static final String COLUMN_DESCRIPTION = "mdescription";
    public static final String COLUMN_DATE = "mdate";
    public static final String COLUMN_LOCATION = "mlocation";
    public static final String COLUMN_IMAGE = "mimage";

    private int id;
    private String title;
    private String place;
    private String description;
    private String date;
    private String location;
    private byte[] image;

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_TITLE + " TEXT," +
                    COLUMN_PLACE + " TEXT," +
                    COLUMN_DESCRIPTION + " TEXT," +
                    COLUMN_DATE + " VARCHAR," +
                    COLUMN_LOCATION + " VARCHAR," +
                    COLUMN_IMAGE + " BLOB" + ")";

    public CheckinModel() {
    }

    public CheckinModel(int id, String title, String place, String description, String date, String location, byte[] image) {
        this.id = id;
        this.title = title;
        this.place = place;
        this.description = description;
        this.date = date;
        this.location = location;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
