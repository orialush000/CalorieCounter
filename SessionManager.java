package com.example.caloriecounter;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREFS_NAME = "MyPrefs";
    private static final String KEY_CURRENT_USER_ID = "current_user_id";

    private Context context;

    public SessionManager(Context context) {
        this.context = context;
    }

    public int getCurrentUserId() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_CURRENT_USER_ID, -1);
    }

    public void setCurrentUserId(int userId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_CURRENT_USER_ID, userId);
        editor.apply();
    }

    public void clearSession() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_CURRENT_USER_ID);
        editor.apply();
    }
}
