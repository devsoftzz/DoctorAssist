package com.devsoftzz.doctorassist.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.devsoftzz.doctorassist.Models.AppointmentPojo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "AppointmentManager.db";
    private static final String TABLE_APPOINMENT = "Appointments";
    private static final String TABLE_NOTIFICATION = "Notification";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_TIME = "time";
    private static final String KEY_DATE = "date";
    private static final String KEY_MORNING = "a";
    private static final String KEY_NOON = "b";
    private static final String KEY_NIGHT = "c";
    private static final String KEY_DAY = "day";
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy'|'HH_mm");
    Context cc;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.cc= context;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_APPOINMENT_TABLE = "CREATE TABLE " + TABLE_APPOINMENT + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_DATE + " TEXT,"
                + KEY_TIME + " TEXT" + ")";
        db.execSQL(CREATE_APPOINMENT_TABLE);

//        String CREATE_NOTIFICATION_TABLE = "CREATE TABLE " + TABLE_NOTIFICATION + "("
//                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_DATE + " TEXT,"
//                + KEY_TIME + " TEXT" + ")";
//        db.execSQL(CREATE_APPOINMENT_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPOINMENT);
        onCreate(db);
    }

    public void addAppoinment(AppointmentPojo appoinmentPojo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, appoinmentPojo.getHospital());
        values.put(KEY_TIME, appoinmentPojo.getTime());
        values.put(KEY_DATE,appoinmentPojo.getDate());
        db.insert(TABLE_APPOINMENT, null, values);
        db.close();
    }

    public List<AppointmentPojo> getAllRecords() throws ParseException {
        List<AppointmentPojo> recordList = new ArrayList<AppointmentPojo>();

        String selectQuery = "SELECT  * FROM " + TABLE_APPOINMENT;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                AppointmentPojo appoinmentPojo = new AppointmentPojo();
                appoinmentPojo.set_id(cursor.getInt(0));
                appoinmentPojo.setHospital(cursor.getString(1));
                appoinmentPojo.setDate(cursor.getString(2));
                appoinmentPojo.setTime(cursor.getString(3));

                Date d2= sdf.parse(cursor.getString(2)+"|"+cursor.getString(3));

                   if(d2.before(new Date()))
                   {
                       deleteRecord(appoinmentPojo);
                   }
                   else
                   {
                       recordList.add(appoinmentPojo);
                   }




            } while (cursor.moveToNext());
        }
        return recordList;
    }

    public void deleteRecord(AppointmentPojo pj){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_APPOINMENT, KEY_ID + " = ?",
                new String[] { String.valueOf(pj.get_id())});
        db.close();
    }

    AppointmentPojo getAppoinment(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_APPOINMENT, new String[] { KEY_ID,
                        KEY_NAME, KEY_DATE, KEY_TIME }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        AppointmentPojo contact = new AppointmentPojo(cursor.getString(0),
                cursor.getString(1),cursor.getString(2));

        return contact;
    }

    void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_APPOINMENT);
        db.close();
    }

}