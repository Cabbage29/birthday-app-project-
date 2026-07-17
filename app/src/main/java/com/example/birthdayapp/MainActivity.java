package com.example.birthdayapp;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etBirthday;
    private TextView tvResult;
    private TextView tvCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        etBirthday = findViewById(R.id.etBirthday);
        tvResult = findViewById(R.id.tvResult);
        tvCount = findViewById(R.id.tvCount);
        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnView = findViewById(R.id.btnView);
        Button btnDelete = findViewById(R.id.btnDelete);
        Button btnReset = findViewById(R.id.btnReset);

        etBirthday.setOnClickListener(v -> openDatePicker());
        btnAdd.setOnClickListener(v -> addBirthday());
        btnView.setOnClickListener(v -> showAllBirthdays());
        btnDelete.setOnClickListener(v -> confirmDeleteAll());
        btnReset.setOnClickListener(v -> resetForm());

        updateBirthdayCount();
    }

    private void openDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                            selectedDay, selectedMonth + 1, selectedYear);
                    etBirthday.setText(date);
                    etBirthday.setError(null);
                },
                year,
                month,
                day
        );

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void addBirthday() {
        String name = etName.getText().toString().trim();
        String birthday = etBirthday.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Please enter a name");
            etName.requestFocus();
            return;
        }

        if (birthday.isEmpty()) {
            etBirthday.setError("Please choose a birthday");
            return;
        }

        ContentValues values = new ContentValues();
        values.put(BirthdayContract.BirthdayEntry.COLUMN_NAME, name);
        values.put(BirthdayContract.BirthdayEntry.COLUMN_BIRTHDAY, birthday);

        Uri result = getContentResolver().insert(BirthdayContract.CONTENT_URI, values);
        if (result == null) {
            Toast.makeText(this, "Unable to save birthday", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Birthday added successfully", Toast.LENGTH_SHORT).show();
        clearInputFields();
        showAllBirthdays();
    }

    private void showAllBirthdays() {
        String[] projection = {
                BirthdayContract.BirthdayEntry._ID,
                BirthdayContract.BirthdayEntry.COLUMN_NAME,
                BirthdayContract.BirthdayEntry.COLUMN_BIRTHDAY
        };

        try (Cursor cursor = getContentResolver().query(
                BirthdayContract.CONTENT_URI,
                projection,
                null,
                null,
                BirthdayContract.BirthdayEntry.COLUMN_NAME + " COLLATE NOCASE ASC")) {

            if (cursor == null || cursor.getCount() == 0) {
                tvResult.setText("No birthdays have been saved yet.");
                tvCount.setText("0 birthdays saved");
                return;
            }

            int nameColumn = cursor.getColumnIndexOrThrow(BirthdayContract.BirthdayEntry.COLUMN_NAME);
            int birthdayColumn = cursor.getColumnIndexOrThrow(BirthdayContract.BirthdayEntry.COLUMN_BIRTHDAY);
            StringBuilder data = new StringBuilder();
            int number = 1;

            while (cursor.moveToNext()) {
                String name = cursor.getString(nameColumn);
                String birthday = cursor.getString(birthdayColumn);
                data.append(number)
                        .append(". ")
                        .append(name)
                        .append("\n   Birthday: ")
                        .append(birthday)
                        .append("\n\n");
                number++;
            }

            tvResult.setText(data.toString().trim());
            tvCount.setText(formatCount(cursor.getCount()));
        }
    }

    private void confirmDeleteAll() {
        int count = getBirthdayCount();
        if (count == 0) {
            Toast.makeText(this, "There are no birthdays to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Delete all birthdays?")
                .setMessage("This will permanently remove all saved birthday records.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> deleteAllBirthdays())
                .show();
    }

    private void deleteAllBirthdays() {
        int deleted = getContentResolver().delete(BirthdayContract.CONTENT_URI, null, null);
        tvResult.setText(R.string.empty_result);
        updateBirthdayCount();
        Toast.makeText(this, deleted + " birthday record(s) deleted", Toast.LENGTH_SHORT).show();
    }

    private void resetForm() {
        clearInputFields();
        tvResult.setText(R.string.empty_result);
        updateBirthdayCount();
        Toast.makeText(this, "Form reset", Toast.LENGTH_SHORT).show();
    }

    private void clearInputFields() {
        etName.setText("");
        etBirthday.setText("");
        etName.setError(null);
        etBirthday.setError(null);
        etName.requestFocus();
        hideKeyboard();
    }

    private void updateBirthdayCount() {
        tvCount.setText(formatCount(getBirthdayCount()));
    }

    private int getBirthdayCount() {
        try (Cursor cursor = getContentResolver().query(
                BirthdayContract.CONTENT_URI,
                new String[]{BirthdayContract.BirthdayEntry._ID},
                null,
                null,
                null)) {
            return cursor == null ? 0 : cursor.getCount();
        }
    }

    private String formatCount(int count) {
        return count == 1 ? "1 birthday saved" : count + " birthdays saved";
    }

    private void hideKeyboard() {
        View currentView = getCurrentFocus();
        if (currentView == null) {
            return;
        }
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(currentView.getWindowToken(), 0);
    }
}
