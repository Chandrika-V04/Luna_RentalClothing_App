package com.example.clothing;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    // Database name and version
    public static final String DB_NAME = "myDB";
    public static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // BOOKINGS TABLE with all required columns
        db.execSQL("CREATE TABLE bookings (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "product TEXT, " +
                "price TEXT, " +
                "date DATE, " +
                "days INTEGER, " +
                "address TEXT, " +
                "total TEXT, " +
                "payment TEXT" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop old table and create a new one
        db.execSQL("DROP TABLE IF EXISTS bookings");
        onCreate(db);
    }
}