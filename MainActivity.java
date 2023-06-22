package com.example.caloriecounter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private static final int NOTIFICATION_ID_1 = 1;
    private static final int NOTIFICATION_ID_2 = 2;
    private static final int NOTIFICATION_ID_3 = 3;

    private TextView txtEaten, txtBurned, txtUserMeasurements, txtRemaining;
    private EditText etFoodAmount, etActivityDuration;
    private Spinner spnFood, spnActivity;
    private Button btnAddFood, btnAddActivity, btnSettings;

    private ProgressBar progressBarCalorie;

    private DatabaseHelper databaseHelper;

    private ApiHandler apiHandler;

    private TextToSpeech textToSpeech;

    private int userId;
    private int caloryGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtEaten = findViewById(R.id.txtEaten);
        txtBurned = findViewById(R.id.txtBurned);
        txtUserMeasurements = findViewById(R.id.textViewUserMeasurements);
        txtRemaining = findViewById(R.id.txtRemaining);
        etFoodAmount = findViewById(R.id.etFoodAmount);
        etActivityDuration = findViewById(R.id.etActivityDuration);
        spnFood = findViewById(R.id.spnFood);
        spnActivity = findViewById(R.id.spnActivity);
        btnAddFood = findViewById(R.id.buttonAddFood);
        btnAddActivity = findViewById(R.id.buttonAddActivity);
        btnSettings = findViewById(R.id.buttonSettings);

        databaseHelper = new DatabaseHelper(this);
        apiHandler = new ApiHandler();

        // Initialize TextToSpeech
        textToSpeech = new TextToSpeech(this, this, "com.google.android.tts");

        progressBarCalorie = findViewById(R.id.progressBarCalorie);
        progressBarCalorie.setMax(100); // Set the maximum value of the progress bar

        userId = getIntent().getIntExtra("user_id", -1);
        caloryGoal = 0;

        if (userId != -1) {
            retrieveEatenAndBurnedValues(userId);
            updateUserMeasurements(userId);
        }

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTime = sdf.format(new Date());

        if (currentTime.equals("00:00")) {
            resetEatenAndBurnedValues(userId);
        }

        calculateAndUpdateRemainingValue();

        Button btnOpenMap = findViewById(R.id.btnOpenMap);
        btnOpenMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });


        btnAddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFoodToDatabase();
            }
        });

        btnAddActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addActivityToDatabase();
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Move to SettingsActivity with the user ID
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                intent.putExtra("user_id", (int) userId);
                startActivity(intent);
                finish(); // Close the current activity
            }
        });
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            // Set language for TextToSpeech
            int result = textToSpeech.setLanguage(Locale.getDefault());

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "TextToSpeech initialization failed.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "TextToSpeech initialization failed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProgressBar(int eatenCalories, int remainingCalories) {
        int progress = (int) ((eatenCalories / (float) (eatenCalories + remainingCalories)) * 100);
        progressBarCalorie.setProgress(progress);
    }

    private void readRemainingCalories(String remainingCalories) {
        textToSpeech.speak("Remaining calories: " + remainingCalories, TextToSpeech.QUEUE_FLUSH, null, null);
    }


    private void retrieveEatenAndBurnedValues(int userId) {
        Cursor cursor = databaseHelper.getUserById(userId);

        if (cursor.moveToFirst()) {
            int emailColumnIndex = cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_EMAIL);
            int eatenColumnIndex = cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_EATEN);
            int burnedColumnIndex = cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_BURNED);

            if (emailColumnIndex != -1 && eatenColumnIndex != -1 && burnedColumnIndex != -1) {
                String email = cursor.getString(emailColumnIndex);
                int eaten = cursor.getInt(eatenColumnIndex);
                int burned = cursor.getInt(burnedColumnIndex);

                txtEaten.setText(eaten + " eaten");
                txtBurned.setText(burned + " burned");
            }
        }

        cursor.close();
    }

    private void updateRemainingValue(int value, int eaten) {
        String remainingText = txtRemaining.getText().toString();
        String remainingDigits = remainingText.replaceAll("\\D+", "");

        int currentRemaining = Integer.parseInt(remainingDigits);
        int updatedRemaining = currentRemaining + value;

        txtRemaining.setText(String.valueOf(updatedRemaining + " remaining"));

        // Read the remaining calories using TextToSpeech
        readRemainingCalories(String.valueOf(updatedRemaining));

        // Call the method to update the progress bar
        if (eaten > 0) {
        updateProgressBar(eaten, updatedRemaining);
        }
    }

    private void calculateAndUpdateRemainingValue() {
        int age;
        String gender;
        double height;
        double weight;
        String goal;

        Cursor cursor = databaseHelper.getUserById(userId);

        if (cursor.moveToFirst()) {
            int ageColumnIndex = cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_AGE);
            int genderColumnIndex = cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_GENDER);
            int heightColumnIndex = cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_HEIGHT);
            int weightColumnIndex = cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_WEIGHT);
            int goalColumnIndex = cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_GOAL);

            if (ageColumnIndex != -1 && genderColumnIndex != -1 && heightColumnIndex != -1 && weightColumnIndex != -1 && goalColumnIndex != -1) {
                age = cursor.getInt(ageColumnIndex);
                gender = cursor.getString(genderColumnIndex);
                height = cursor.getDouble(heightColumnIndex);
                weight = cursor.getDouble(weightColumnIndex);
                goal = cursor.getString(goalColumnIndex);

                String URL = "https://fitness-calculator.p.rapidapi.com/dailycalorie?age=" + age + "&gender=" + gender + "&height=" + height + "&weight=" + weight + "&activitylevel=level_1";

                // Make API request to retrieve calorie information on a background thread
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpClient client = new OkHttpClient();

                        Request request = new Request.Builder()
                                .url("https://fitness-calculator.p.rapidapi.com/dailycalorie?age=25&gender=male&height=180&weight=70&activitylevel=level_1")
                                .get()
                                .addHeader("X-RapidAPI-Key", "ae220d63bcmsha0ba9a7f32fa55cp15714ejsn06c8769981c6")
                                .addHeader("X-RapidAPI-Host", "fitness-calculator.p.rapidapi.com")
                                .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                e.printStackTrace();
                                // Handle exception
                                // Display an error message or perform appropriate error handling
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if (response.isSuccessful()) {
                                    String responseBody = response.body().string();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            System.out.println(responseBody);

                                            try {
                                                JSONObject jsonObject = new JSONObject(responseBody);
                                                JSONObject goals = jsonObject.getJSONObject("data").getJSONObject("goals");

                                                if (goal.equals("Lose weight")) {
                                                    caloryGoal = goals.getJSONObject("Weight loss").getInt("calory");
                                                }
                                                else if (goal.equals("Gain weight")) {
                                                    caloryGoal = goals.getJSONObject("Weight gain").getInt("calory");
                                                }
                                                else {
                                                    caloryGoal = goals.getInt("maintain weight");
                                                }

                                                TextView txtRemaining = findViewById(R.id.txtRemaining);
                                                txtRemaining.setText(String.valueOf(caloryGoal + " remaining"));

                                                // Read the remaining calories using TextToSpeech
                                                readRemainingCalories(String.valueOf(caloryGoal));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                }).start();
            }
        }

        cursor.close();
    }

    private void updateUserMeasurements(int userId) {
        Cursor cursor = databaseHelper.getUserById(userId);

        if (cursor.moveToFirst()) {
            int heightColumnIndex = cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_HEIGHT);
            int weightColumnIndex = cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_WEIGHT);

            if (heightColumnIndex != -1 && weightColumnIndex != -1) {
                int height = cursor.getInt(heightColumnIndex);
                int weight = cursor.getInt(weightColumnIndex);

                txtUserMeasurements.setText(height + " height " + weight + " weight");
            }
        }

        cursor.close();
    }

    private void resetEatenAndBurnedValues(int userId) {
        databaseHelper.updateEatenAndBurnedValues(userId, 0, 0);
        txtEaten.setText("0 eaten");
        txtBurned.setText("0 burned");
    }

    private void addFoodToDatabase() {
        String food = spnFood.getSelectedItem().toString();
        String amountString = etFoodAmount.getText().toString();

        if (food.isEmpty() || amountString.isEmpty()) {
            Toast.makeText(this, "Please enter both food and amount", Toast.LENGTH_SHORT).show();
            return;
        }

        int amount = Integer.parseInt(amountString);

        int calorieAmount = apiHandler.getFoodCalorie(food, amount);

        if (calorieAmount != -1) {
            int eaten = databaseHelper.updateEatenValue(userId, calorieAmount);
            updateRemainingValue(-1 * calorieAmount, eaten);
            Toast.makeText(MainActivity.this, "Food added successfully", Toast.LENGTH_SHORT).show();
            retrieveEatenAndBurnedValues(userId);

        } else {
            Toast.makeText(MainActivity.this, "Failed to retrieve calorie", Toast.LENGTH_SHORT).show();
        }
    }

    private void addActivityToDatabase() {
        String activity = spnActivity.getSelectedItem().toString();
        String durationString = etActivityDuration.getText().toString();

        if (activity.isEmpty() || durationString.isEmpty()) {
            Toast.makeText(this, "Please enter both activity and duration", Toast.LENGTH_SHORT).show();
            return;
        }

        int duration = Integer.parseInt(durationString);

        int calorieAmount = apiHandler.getActivityCalorie(activity, duration);

        if (calorieAmount != -1) {
            databaseHelper.updateBurnedValue(userId, calorieAmount);
            updateRemainingValue(calorieAmount, 0);
            Toast.makeText(MainActivity.this, "Activity added successfully", Toast.LENGTH_SHORT).show();
            retrieveEatenAndBurnedValues(userId);
        } else {
            Toast.makeText(MainActivity.this, "Failed to retrieve burned calories", Toast.LENGTH_SHORT).show();
        }
    }
}
