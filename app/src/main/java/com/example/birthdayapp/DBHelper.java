package com.example.birthdayapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "BirthdayDB.db";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlCreate = "CREATE TABLE "
                + BirthdayContract.BirthdayEntry.TABLE_NAME + " ("
                + BirthdayContract.BirthdayEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BirthdayContract.BirthdayEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + BirthdayContract.BirthdayEntry.COLUMN_BIRTHDAY + " TEXT NOT NULL)";
        db.execSQL(sqlCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + BirthdayContract.BirthdayEntry.TABLE_NAME);
        onCreate(db);
    }
}
