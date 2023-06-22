package com.example.caloriecounter;

public final class DatabaseContract {

    private DatabaseContract() {
    }

    public static class UserEntry {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_AGE = "age";
        public static final String COLUMN_GENDER = "gender";
        public static final String COLUMN_GOAL = "goal";
        public static final String COLUMN_HEIGHT = "height";
        public static final String COLUMN_WEIGHT = "weight";
        public static final String COLUMN_EATEN = "eaten";
        public static final String COLUMN_BURNED = "burned";

        public static final String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EMAIL + " TEXT NOT NULL, " +
                COLUMN_PASSWORD + " TEXT NOT NULL, " +
                COLUMN_AGE + " INTEGER NOT NULL, " +
                COLUMN_GENDER + " TEXT NOT NULL, " +
                COLUMN_GENDER + " TEXT NOT NULL, " +
                COLUMN_HEIGHT + " REAL NOT NULL, " +
                COLUMN_WEIGHT + " REAL NOT NULL, " +
                COLUMN_EATEN + " INTEGER DEFAULT 0, " +
                COLUMN_BURNED + " INTEGER DEFAULT 0)";

        public static final String DELETE_TABLE_QUERY = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
