package com.example.birthdayapp;

import android.net.Uri;
import android.provider.BaseColumns;

public final class BirthdayContract {

    private BirthdayContract() {
    }

    public static final String AUTHORITY = "com.example.birthdayapp.provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_BIRTHDAYS = "birthdays";
    public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BIRTHDAYS);

    public static final class BirthdayEntry implements BaseColumns {
        private BirthdayEntry() {
        }

        public static final String TABLE_NAME = "birthdays";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_BIRTHDAY = "birthday";
    }
}
