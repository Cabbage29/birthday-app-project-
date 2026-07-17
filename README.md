# BirthdayApp – IAS3153 Lab Exercise 2

A Java Android application that stores birthday records in SQLite through a custom Content Provider.

## Features

- Add a person's name and birthday
- DatePickerDialog for choosing a date
- Display all saved birthday records
- Delete all records with confirmation
- Reset the input form
- Input validation
- Saved-record counter
- Improved purple interface

## Build the APK with GitHub Actions

1. Upload every file and folder in this project to the root of a GitHub repository.
2. Open **Actions**.
3. Open **Build BirthdayApp APK**.
4. Click **Run workflow**, or push a commit to `main`.
5. Open the successful run and download **BirthdayApp-debug-apk** under Artifacts.
6. Extract the artifact to obtain `app-debug.apk`.

The repository root must directly contain `settings.gradle`, `build.gradle`, `app`, and `.github`.
