package com.example.virginia.cs.edu.politoed;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.virginia.cs.edu.politoed.Alarm;

import org.apache.http.cookie.CookieAttributeHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Neal on 11/10/2014.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "AlarmApp";
    private static final int DB_VERSION = 1;

    private static final String TABLE_ALARMS = "Alarms";
    private static final String FIELD_ID = "id";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_DATEYEAR = "dateYear";
    private static final String FIELD_DATEMONTH = "dateMonth";
    private static final String FIELD_DATEDAY = "dateDay";
    private static final String FIELD_TIMEHOUR = "timeHour";
    private static final String FIELD_TIMEMINUTE = "timeMinute";
    private static final String FIELD_CATEGORY = "category";
    private static final String FIELD_PRIORITY = "priority";
    private static final String FIELD_NOTES = "notes";
    private static final String[] FIELDS = {
            FIELD_ID, FIELD_NAME, FIELD_DATEYEAR, FIELD_DATEMONTH, FIELD_DATEDAY,
            FIELD_TIMEHOUR, FIELD_TIMEMINUTE, FIELD_CATEGORY, FIELD_PRIORITY, FIELD_NOTES
    };

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String queryFmt = "CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, " +
                "%s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER, " +
                "%s INTEGER, %s TEXT);";
        String query = String.format(queryFmt, TABLE_ALARMS, FIELD_ID, FIELD_NAME, FIELD_DATEYEAR,
                FIELD_DATEMONTH, FIELD_DATEDAY, FIELD_TIMEHOUR, FIELD_TIMEMINUTE, FIELD_CATEGORY,
                FIELD_PRIORITY, FIELD_NOTES);
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = String.format("DROP TABLE IF EXISTS %s;", TABLE_ALARMS);
        db.execSQL(query);

        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void addAlarm(Alarm alarm) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FIELD_NAME, alarm.getName());
        values.put(FIELD_DATEYEAR, alarm.getDateYear());
        values.put(FIELD_DATEMONTH, alarm.getDateMonth());
        values.put(FIELD_DATEDAY, alarm.getDateDay());
        values.put(FIELD_TIMEHOUR, alarm.getTimeHour());
        values.put(FIELD_TIMEMINUTE, alarm.getTimeMinute());
        values.put(FIELD_CATEGORY, alarm.getCategory());
        values.put(FIELD_PRIORITY, alarm.getPriority());
        values.put(FIELD_NOTES, alarm.getNotes());

        db.insert(TABLE_ALARMS, null, values);
    }

    public Alarm getAlarm(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] selection = FIELDS.clone();
        String[] selectionArgs = {String.valueOf(id)};

        Cursor c = db.query(TABLE_ALARMS, selection, String.format("%s=?", FIELD_ID), selectionArgs,
                null, null, null);

        if (c == null || !c.moveToFirst()) {
            return null;
        } else {
            return new Alarm(c.getInt(0), c.getString(1), c.getInt(2), c.getInt(3), c.getInt(4),
                    c.getInt(5), c.getInt(6), c.getInt(7), c.getInt(8), c.getString(9));
        }
    }

    public List<Alarm> getAllAlarms() {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] selection = {
                FIELD_ID, FIELD_NAME, FIELD_DATEYEAR, FIELD_DATEMONTH, FIELD_DATEDAY,
                FIELD_TIMEHOUR, FIELD_TIMEMINUTE, FIELD_CATEGORY, FIELD_PRIORITY, FIELD_NOTES
        };

        String query = String.format("SELECT * FROM %s;", TABLE_ALARMS);
        Cursor c = db.rawQuery(query, null);

        if (c == null || !c.moveToFirst()) {
            return null;
        } else {
            List<Alarm> result = new ArrayList<Alarm>();
            do {
                Alarm alarm = new Alarm(c.getInt(0), c.getString(1), c.getInt(2), c.getInt(3),
                        c.getInt(4), c.getInt(5), c.getInt(6), c.getInt(7), c.getInt(8),
                        c.getString(9));
                result.add(alarm);
            } while (c.moveToNext());
            return result;
        }
    }

    public int getAlarmCount() {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = String.format("SELECT  * FROM %s;", TABLE_ALARMS);
        Cursor c = db.rawQuery(query, null);
        return c.getCount();
    }

    public int updateAlarm(Alarm alarm) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FIELD_NAME, alarm.getName());
        values.put(FIELD_DATEYEAR, alarm.getDateYear());
        values.put(FIELD_DATEMONTH, alarm.getDateMonth());
        values.put(FIELD_DATEDAY, alarm.getDateDay());
        values.put(FIELD_TIMEHOUR, alarm.getTimeHour());
        values.put(FIELD_TIMEMINUTE, alarm.getTimeMinute());
        values.put(FIELD_CATEGORY, alarm.getCategory());
        values.put(FIELD_PRIORITY, alarm.getPriority());
        values.put(FIELD_NOTES, alarm.getNotes());

        String[] selectionArgs = {String.valueOf(alarm.getId())};
        return db.update(TABLE_ALARMS, values, String.format("%s=?", FIELD_ID), selectionArgs);
    }

    public void deleteAlarm(Alarm alarm) {
        SQLiteDatabase db = this.getWritableDatabase();

        String[] selectionArgs = {String.valueOf(alarm.getId())};
        db.delete(TABLE_ALARMS, String.format("%s=?", FIELD_ID), selectionArgs);
    }
}