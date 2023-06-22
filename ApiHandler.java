package com.example.caloriecounter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ApiHandler {

    private Map<String, Integer> foodToCal;
    private Map<String, Integer> actToCal;

    public ApiHandler() {
        foodToCal = new HashMap<>();
        foodToCal.put("Apple", 52);
        foodToCal.put("Egg", 88);

        actToCal = new HashMap<>();
        actToCal.put("Running", 11);
        actToCal.put("Lifting", 4);
    }

    public int getFoodCalorie(String food, int amount) {
        if (foodToCal.containsKey(food)) {
            int caloriePerUnit = foodToCal.get(food);
            return caloriePerUnit * amount;
        } else {
            return -1;
        }
    }

    public int getActivityCalorie(String activity, int duration) {
        if (actToCal.containsKey(activity)) {
            int caloriePerHour = actToCal.get(activity);
            return caloriePerHour * duration;
        } else {
            return -1;
        }
    }

    public static String getMacroCalculatorData(int age, String gender, double height, double weight, int activityLevel, String goal) {
        String apiUrl = "https://fitness-calculator.p.rapidapi.com/macrocalculator" +
                "?age=" + age +
                "&gender=" + gender +
                "&height=" + height +
                "&weight=" + weight +
                "&activitylevel=" + activityLevel +
                "&goal=" + goal;

        String apiKey = "ae220d63bcmsha0ba9a7f32fa55cp15714ejsn06c8769981c6";
        String apiHost = "fitness-calculator.p.rapidapi.com";

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-RapidAPI-Key", apiKey);
            connection.setRequestProperty("X-RapidAPI-Host", apiHost);

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                return response.toString();
            } else {
                System.out.println("Failed to retrieve macro calculator data. Response code: " + responseCode);
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}







