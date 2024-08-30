package com.example.edubridge;
import android.content.Context;
import android.content.SharedPreferences;
public class UserRole {
    private static final String PREF_NAME = "user_profile";
    private static final String KEY_ROLE = "user_role";

    // Method to save user role in SharedPreferences
    public static void saveUserRole(Context context, String role) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_ROLE, role);
        editor.apply();
    }

    // Method to get user role from SharedPreferences
    public static String getUserRole(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString(KEY_ROLE, null); // Returns null if no role is found
    }
}
