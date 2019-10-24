package com.checkin.mycheckins.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import com.checkin.mycheckins.Models.CheckinModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 7;
    // Database Name
    private static final String DATABASE_NAME = "check_in_db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // create table
        db.execSQL(CheckinModel.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + CheckinModel.TABLE_NAME);

        // Create table again
        onCreate(db);
    }

    public long insertCheckIn(String title, String place, String description, String location, String mdate, byte[] image) {

        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        // `id` will be inserted automatically.
        values.put(CheckinModel.COLUMN_TITLE, title);
        values.put(CheckinModel.COLUMN_PLACE, place);
        values.put(CheckinModel.COLUMN_DESCRIPTION, description);
        values.put(CheckinModel.COLUMN_LOCATION, location);
        values.put(CheckinModel.COLUMN_DATE, mdate);
        values.put(CheckinModel.COLUMN_IMAGE, image);

        // insert row
        long id = db.insert(CheckinModel.TABLE_NAME, null, values);

        // close db connection
        db.close();

        Log.i("dsdsadasdsa", String.valueOf(id));
        // return newly inserted row id
        return id;
    }

    public int getCheckInCount() {
        String countQuery = "SELECT  * FROM " + CheckinModel.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    public CheckinModel getCheckIn(long id) {

        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(CheckinModel.TABLE_NAME,
                new String[]{CheckinModel.COLUMN_ID, CheckinModel.COLUMN_TITLE, CheckinModel.COLUMN_PLACE
                        , CheckinModel.COLUMN_DESCRIPTION, CheckinModel.COLUMN_LOCATION
                        , CheckinModel.COLUMN_DATE, CheckinModel.COLUMN_IMAGE},
                CheckinModel.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare checkIn object
        CheckinModel checkIn = new CheckinModel(
                cursor.getInt(cursor.getColumnIndex(CheckinModel.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(CheckinModel.COLUMN_TITLE)),
                cursor.getString(cursor.getColumnIndex(CheckinModel.COLUMN_PLACE)),
                cursor.getString(cursor.getColumnIndex(CheckinModel.COLUMN_DESCRIPTION)),
                cursor.getString(cursor.getColumnIndex(CheckinModel.COLUMN_LOCATION)),
                cursor.getString(cursor.getColumnIndex(CheckinModel.COLUMN_DATE)),
                cursor.getBlob(cursor.getColumnIndex(CheckinModel.COLUMN_IMAGE)));

        // close the db connection
        cursor.close();

        return checkIn;
    }

    public List<CheckinModel> getAllCheckins() {
        List<CheckinModel> checkins = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + CheckinModel.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                CheckinModel checkIn = new CheckinModel();
                checkIn.setId(cursor.getInt(cursor.getColumnIndex(CheckinModel.COLUMN_ID)));
                checkIn.setTitle(cursor.getString(cursor.getColumnIndex(CheckinModel.COLUMN_TITLE)));
                checkIn.setPlace(cursor.getString(cursor.getColumnIndex(CheckinModel.COLUMN_PLACE)));
                checkIn.setDescription(cursor.getString(cursor.getColumnIndex(CheckinModel.COLUMN_DESCRIPTION)));
                checkIn.setLocation(cursor.getString(cursor.getColumnIndex(CheckinModel.COLUMN_LOCATION)));
                checkIn.setDate(cursor.getString(cursor.getColumnIndex(CheckinModel.COLUMN_DATE)));
                checkIn.setImage(cursor.getBlob(cursor.getColumnIndex(CheckinModel.COLUMN_IMAGE)));

                checkins.add(checkIn);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return checkin list
        return checkins;
    }

    public int updateCheckin(CheckinModel checkIn) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CheckinModel.COLUMN_TITLE, checkIn.getTitle());
        values.put(CheckinModel.COLUMN_PLACE, checkIn.getPlace());
        values.put(CheckinModel.COLUMN_DESCRIPTION, checkIn.getDescription());
        values.put(CheckinModel.COLUMN_DATE, checkIn.getDate());
        values.put(CheckinModel.COLUMN_IMAGE, checkIn.getImage());
        values.put(CheckinModel.COLUMN_LOCATION, checkIn.getLocation());

        // updating row
        return db.update(CheckinModel.TABLE_NAME, values, CheckinModel.COLUMN_ID + " = ? " /*+ checkIn.getId() */,
                new String[]{String.valueOf(checkIn.getId())});
    }

    public void deleteCheckIn(CheckinModel note) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(CheckinModel.TABLE_NAME, CheckinModel.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
        db.close();
    }
}
