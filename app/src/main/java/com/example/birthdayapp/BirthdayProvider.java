package com.example.birthdayapp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class BirthdayProvider extends ContentProvider {

    private static final int BIRTHDAYS = 100;
    private static final int BIRTHDAY_ID = 101;

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(BirthdayContract.AUTHORITY, BirthdayContract.PATH_BIRTHDAYS, BIRTHDAYS);
        URI_MATCHER.addURI(BirthdayContract.AUTHORITY, BirthdayContract.PATH_BIRTHDAYS + "/#", BIRTHDAY_ID);
    }

    private DBHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getRequiredContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(BirthdayContract.BirthdayEntry.TABLE_NAME);

        int match = URI_MATCHER.match(uri);
        if (match == BIRTHDAY_ID) {
            queryBuilder.appendWhere(BirthdayContract.BirthdayEntry._ID + "=");
            queryBuilder.appendWhereEscapeString(uri.getLastPathSegment());
        } else if (match != BIRTHDAYS) {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs,
                null, null, sortOrder);
        cursor.setNotificationUri(getRequiredContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (URI_MATCHER.match(uri) != BIRTHDAYS) {
            throw new IllegalArgumentException("Insertion is not supported for URI: " + uri);
        }

        String name = values.getAsString(BirthdayContract.BirthdayEntry.COLUMN_NAME);
        String birthday = values.getAsString(BirthdayContract.BirthdayEntry.COLUMN_BIRTHDAY);
        if (name == null || name.trim().isEmpty() || birthday == null || birthday.trim().isEmpty()) {
            throw new IllegalArgumentException("Name and birthday are required");
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = db.insert(BirthdayContract.BirthdayEntry.TABLE_NAME, null, values);
        if (id == -1) {
            return null;
        }

        Uri insertedUri = ContentUris.withAppendedId(BirthdayContract.CONTENT_URI, id);
        getRequiredContext().getContentResolver().notifyChange(insertedUri, null);
        return insertedUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted;

        int match = URI_MATCHER.match(uri);
        if (match == BIRTHDAYS) {
            rowsDeleted = db.delete(BirthdayContract.BirthdayEntry.TABLE_NAME, selection, selectionArgs);
        } else if (match == BIRTHDAY_ID) {
            String id = uri.getLastPathSegment();
            rowsDeleted = db.delete(BirthdayContract.BirthdayEntry.TABLE_NAME,
                    BirthdayContract.BirthdayEntry._ID + "=?", new String[]{id});
        } else {
            throw new IllegalArgumentException("Deletion is not supported for URI: " + uri);
        }

        if (rowsDeleted > 0) {
            getRequiredContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsUpdated;

        int match = URI_MATCHER.match(uri);
        if (match == BIRTHDAYS) {
            rowsUpdated = db.update(BirthdayContract.BirthdayEntry.TABLE_NAME,
                    values, selection, selectionArgs);
        } else if (match == BIRTHDAY_ID) {
            String id = uri.getLastPathSegment();
            rowsUpdated = db.update(BirthdayContract.BirthdayEntry.TABLE_NAME,
                    values, BirthdayContract.BirthdayEntry._ID + "=?", new String[]{id});
        } else {
            throw new IllegalArgumentException("Update is not supported for URI: " + uri);
        }

        if (rowsUpdated > 0) {
            getRequiredContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public String getType(Uri uri) {
        int match = URI_MATCHER.match(uri);
        if (match == BIRTHDAYS) {
            return "vnd.android.cursor.dir/" + BirthdayContract.AUTHORITY + ".birthdays";
        }
        if (match == BIRTHDAY_ID) {
            return "vnd.android.cursor.item/" + BirthdayContract.AUTHORITY + ".birthdays";
        }
        throw new IllegalArgumentException("Unknown URI: " + uri);
    }

    private android.content.Context getRequiredContext() {
        android.content.Context context = getContext();
        if (context == null) {
            throw new IllegalStateException("ContentProvider is not attached to a context");
        }
        return context;
    }
}
