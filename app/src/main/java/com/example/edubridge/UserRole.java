package com.example.edubridge;
import android.content.Context;
import android.content.SharedPreferences;
public class UserRole {
    private static final String PREFS_NAME = "UserRolePrefs";
    private static final String ROLE_KEY = "user_role";

    public static void saveUserRole(Context context, String role) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(ROLE_KEY, role).apply();
    }

    public static String getUserRole(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(ROLE_KEY, null);
    }
}
