package com.example.caloriecounter;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private EditText edtAge, edtHeight, edtWeight;
    private Spinner spnGender, spnGoal;
    private Button btnSaveChanges;
    private DatabaseHelper databaseHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        databaseHelper = new DatabaseHelper(this);
        userId = getIntent().getIntExtra("user_id", -1);

        edtAge = findViewById(R.id.edtAge);
        edtHeight = findViewById(R.id.edtHeight);
        edtWeight = findViewById(R.id.edtWeight);
        spnGender = findViewById(R.id.spnGender);
        spnGoal = findViewById(R.id.spnGoal);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

        loadUserData();
    }

    private void loadUserData() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String[] projection = {
                DatabaseContract.UserEntry.COLUMN_AGE,
                DatabaseContract.UserEntry.COLUMN_GENDER,
                DatabaseContract.UserEntry.COLUMN_HEIGHT,
                DatabaseContract.UserEntry.COLUMN_WEIGHT,
                DatabaseContract.UserEntry.COLUMN_GOAL
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

        if (cursor.moveToFirst()) {

            int ageIndex = cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_AGE);
            int genderIndex = cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_GENDER);
            int heightIndex = cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_HEIGHT);
            int weightIndex = cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_WEIGHT);
            int goalIndex = cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_GOAL);

            if (ageIndex != -1 && genderIndex != -1 && heightIndex != -1 && weightIndex != -1 && goalIndex != -1) {
                int age = cursor.getInt(ageIndex);
                String gender = cursor.getString(genderIndex);
                double height = cursor.getDouble(heightIndex);
                double weight = cursor.getDouble(weightIndex);
                String goal = cursor.getString(goalIndex);

                edtAge.setText(String.valueOf(age));
                edtHeight.setText(String.valueOf(height));
                edtWeight.setText(String.valueOf(weight));

                // Set the selected item in the gender spinner
                String[] genders = getResources().getStringArray(R.array.gender_options);
                for (int i = 0; i < genders.length; i++) {
                    if (genders[i].equalsIgnoreCase(gender)) {
                        spnGender.setSelection(i);
                        break;
                    }
                }

                // Set the selected item in the goal spinner
                String[] goals = getResources().getStringArray(R.array.goal_options);
                for (int i = 0; i < goals.length; i++) {
                    if (goals[i].equalsIgnoreCase(goal)) {
                        spnGoal.setSelection(i);
                        break;
                    }
                }
            }
        }

        cursor.close();
    }

    private void saveChanges() {
        int age = Integer.parseInt(edtAge.getText().toString());
        double height = Double.parseDouble(edtHeight.getText().toString());
        double weight = Double.parseDouble(edtWeight.getText().toString());
        String gender = spnGender.getSelectedItem().toString();
        String goal = spnGoal.getSelectedItem().toString();

        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.UserEntry.COLUMN_AGE, age);
        values.put(DatabaseContract.UserEntry.COLUMN_GENDER, gender);
        values.put(DatabaseContract.UserEntry.COLUMN_HEIGHT, height);
        values.put(DatabaseContract.UserEntry.COLUMN_WEIGHT, weight);
        values.put(DatabaseContract.UserEntry.COLUMN_GOAL, goal);

        String selection = DatabaseContract.UserEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};

        int rowsAffected = db.update(
                DatabaseContract.UserEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        if (rowsAffected > 0) {
            Toast.makeText(this, "Changes saved successfully", Toast.LENGTH_SHORT).show();

            // Move to MainActivity with the user ID
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.putExtra("user_id", (int) userId);
            startActivity(intent);
            finish(); // Close the current activity


        } else {
            Toast.makeText(this, "Failed to save changes", Toast.LENGTH_SHORT).show();
        }
    }
}
