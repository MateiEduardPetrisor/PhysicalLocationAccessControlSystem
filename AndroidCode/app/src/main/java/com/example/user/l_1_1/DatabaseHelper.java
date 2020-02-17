package com.example.user.l_1_1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 2;
    private static final String DATABASE_NAME = "db.db";
    private static final String T_EVENTS = "EVENTS";
    private static final String EVENTS_ID = "E_ID";
    private static final String EVENTS_MESSAGE = "E_MESSAGE";


    private static final String TABLE_CREATE_EVENTS = "CREATE TABLE " + T_EVENTS
            + "("
            + EVENTS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + EVENTS_MESSAGE + " TEXT NOT NULL"
            + ");";

    SQLiteDatabase db;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_EVENTS);
        this.db = db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertEvent(Message m) throws Exception {
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(EVENTS_MESSAGE, m.toString());
        db.insert(T_EVENTS, null, cv);
        db.close();
    }

    public List<String> getEventsInString() throws Exception {
        List<String> lstMessages = new ArrayList<>();
        db = this.getReadableDatabase();
        String querry = "SELECT * FROM " + T_EVENTS + ";";
        Cursor cursor = db.rawQuery(querry, null);
        cursor.moveToFirst();
        do {
            String message = "";
            String tmp = cursor.getString(1);
            String[] values = tmp.split(";");

            String[] arduinoValues = values[0].split(",");
            int zone = Integer.parseInt(arduinoValues[0]);
            String messageText = arduinoValues[1];
            message = message + "Zona ";
            message = message + zone + " ";
            message = message + messageText + " ";
            String timeStamp = values[1];
            DateTime dateTime = new DateTime(timeStamp);
            message = message + dateTime;
            lstMessages.add(message);
        } while (cursor.moveToNext());
        cursor.close();
        return lstMessages;
    }

    public void clearEvents() {
        db = this.getWritableDatabase();
        String querry = "DROP TABLE " + T_EVENTS + ";";
        db.execSQL(querry);
        onCreate(db);
    }
}