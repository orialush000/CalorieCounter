package com.example.caloriecounter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "calorie_counter.db";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_TABLE_USER =
            "CREATE TABLE " + DatabaseContract.UserEntry.TABLE_NAME + " (" +
                    DatabaseContract.UserEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DatabaseContract.UserEntry.COLUMN_EMAIL + " TEXT NOT NULL," +
                    DatabaseContract.UserEntry.COLUMN_PASSWORD + " TEXT NOT NULL," +
                    DatabaseContract.UserEntry.COLUMN_AGE + " INTEGER NOT NULL," +
                    DatabaseContract.UserEntry.COLUMN_GENDER + " TEXT NOT NULL," +
                    DatabaseContract.UserEntry.COLUMN_GOAL + " TEXT NOT NULL," +
                    DatabaseContract.UserEntry.COLUMN_HEIGHT + " REAL NOT NULL," +
                    DatabaseContract.UserEntry.COLUMN_WEIGHT + " REAL NOT NULL," +
                    DatabaseContract.UserEntry.COLUMN_EATEN + " INTEGER DEFAULT 0," +
                    DatabaseContract.UserEntry.COLUMN_BURNED + " INTEGER DEFAULT 0)";

    private static final String DROP_TABLE_USER =
            "DROP TABLE IF EXISTS " + DatabaseContract.UserEntry.TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_USER);
        onCreate(db);
    }

    public long addUser(String email, String password, int age, String gender, String goal, double height, double weight) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.UserEntry.COLUMN_EMAIL, email);
        values.put(DatabaseContract.UserEntry.COLUMN_PASSWORD, password);
        values.put(DatabaseContract.UserEntry.COLUMN_AGE, age);
        values.put(DatabaseContract.UserEntry.COLUMN_GENDER, gender);
        values.put(DatabaseContract.UserEntry.COLUMN_GOAL, goal);
        values.put(DatabaseContract.UserEntry.COLUMN_HEIGHT, height);
        values.put(DatabaseContract.UserEntry.COLUMN_WEIGHT, weight);

        long userId = db.insert(DatabaseContract.UserEntry.TABLE_NAME, null, values);

        db.close();

        return userId;
    }

    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                DatabaseContract.UserEntry.COLUMN_ID,
                DatabaseContract.UserEntry.COLUMN_EMAIL,
                DatabaseContract.UserEntry.COLUMN_PASSWORD,
                DatabaseContract.UserEntry.COLUMN_AGE,
                DatabaseContract.UserEntry.COLUMN_GENDER,
                DatabaseContract.UserEntry.COLUMN_GOAL,
                DatabaseContract.UserEntry.COLUMN_HEIGHT,
                DatabaseContract.UserEntry.COLUMN_WEIGHT,
                DatabaseContract.UserEntry.COLUMN_EATEN,
                DatabaseContract.UserEntry.COLUMN_BURNED
        };

        String selection = DatabaseContract.UserEntry.COLUMN_EMAIL + " = ?";
        String[] selectionArgs = {email};

        Cursor cursor = db.query(
                DatabaseContract.UserEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        return cursor;
    }


    public Cursor getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                DatabaseContract.UserEntry.COLUMN_ID,
                DatabaseContract.UserEntry.COLUMN_EMAIL,
                DatabaseContract.UserEntry.COLUMN_PASSWORD,
                DatabaseContract.UserEntry.COLUMN_AGE,
                DatabaseContract.UserEntry.COLUMN_GENDER,
                DatabaseContract.UserEntry.COLUMN_GOAL,
                DatabaseContract.UserEntry.COLUMN_HEIGHT,
                DatabaseContract.UserEntry.COLUMN_WEIGHT,
                DatabaseContract.UserEntry.COLUMN_EATEN,
                DatabaseContract.UserEntry.COLUMN_BURNED
        };

        String selection = DatabaseContract.UserEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};

        Cursor cursor = db.query(
                DatabaseContract.UserEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        return cursor;
    }

    public void updateEatenAndBurnedValues(int userId, int eaten, int burned) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.UserEntry.COLUMN_EATEN, eaten);
        values.put(DatabaseContract.UserEntry.COLUMN_BURNED, burned);

        String selection = DatabaseContract.UserEntry.COLUMN_ID + "=?";
        String[] selectionArgs = {String.valueOf(userId)};

        db.update(DatabaseContract.UserEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    public int updateEatenValue(int userId, int eaten) {
        SQLiteDatabase db = getWritableDatabase();

        // Get the existing eaten value from the database
        Cursor cursor = getUserById(userId);
        int existingEaten = 0;
        if (cursor.moveToFirst()) {
            int eatenColumnIndex = cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_EATEN);
            existingEaten = cursor.getInt(eatenColumnIndex);
        }
        cursor.close();

        // Calculate the new total eaten value
        int newEaten = existingEaten + eaten;

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.UserEntry.COLUMN_EATEN, newEaten);

        String selection = DatabaseContract.UserEntry.COLUMN_ID + "=?";
        String[] selectionArgs = {String.valueOf(userId)};

        db.update(DatabaseContract.UserEntry.TABLE_NAME, values, selection, selectionArgs);

        return newEaten;
    }

    public void updateBurnedValue(int userId, int burned) {
        SQLiteDatabase db = getWritableDatabase();

        // Get the existing burned value from the database
        Cursor cursor = getUserById(userId);
        int existingBurned = 0;
        if (cursor.moveToFirst()) {
            int burnedColumnIndex = cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_BURNED);
            existingBurned = cursor.getInt(burnedColumnIndex);
        }
        cursor.close();

        // Calculate the new total burned value
        int newBurned = existingBurned + burned;

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.UserEntry.COLUMN_BURNED, newBurned);

        String selection = DatabaseContract.UserEntry.COLUMN_ID + "=?";
        String[] selectionArgs = {String.valueOf(userId)};

        db.update(DatabaseContract.UserEntry.TABLE_NAME, values, selection, selectionArgs);
    }

}

