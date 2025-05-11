package com.example.edubridge;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.edubridge.Chat.ChatActivity;
import com.example.edubridge.Chat.Chats;
import com.example.edubridge.Model.UserModel;
import com.example.edubridge.School.SchoolHome;
import com.example.edubridge.Teacher.TeacherHome;
import com.example.edubridge.Utils.AndroidUtils;
import com.example.edubridge.Utils.FireBaseUtil;

public class SplashScreen extends AppCompatActivity {
    private static final int SPLASH_DISPLAY_LENGTH = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        LinearLayout linearLayout = findViewById(R.id.lay);

        // Load the combined animation
        Animation fadeZoomAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_zoom);

        // Apply the animation to the LinearLayout
        linearLayout.startAnimation(fadeZoomAnimation);

        // Check if the user is logged in and handle navigation
        checkUserLoggedIn();

        // Handle system bars (e.g., status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Checks if the user is logged in and navigates accordingly.
     */
    private void checkUserLoggedIn() {
        if (!FireBaseUtil.isLoogedIn()) {
            // User is not logged in, redirect to MainActivity
            Intent loginIntent = new Intent(SplashScreen.this, MainActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
            return;
        }

        // User is logged in, handle notification or default navigation
        if (getIntent().getExtras() != null) {
            // Handle notification
            handleNotification();
        } else {
            // No notification, proceed with default navigation after splash delay
            new Handler().postDelayed(() -> {
                navigateBasedOnUserRole();
            }, SPLASH_DISPLAY_LENGTH);
        }
    }

    /**
     * Navigates to the appropriate home screen based on user role
     */
    private void navigateBasedOnUserRole() {
        String userRole = UserRole.getUserRole(this);

        Intent mainIntent;
        if (userRole != null && userRole.equals("teacher")) {
            mainIntent = new Intent(SplashScreen.this, TeacherHome.class);
        } else {
            // Assuming you have a SchoolHome activity for non-teacher users
            mainIntent = new Intent(SplashScreen.this, SchoolHome.class);
        }

        startActivity(mainIntent);
        finish();
    }

    /**
     * Handles navigation when the app is launched from a notification.
     */
    private void handleNotification() {
        String uid = getIntent().getExtras().getString("userId");
        if (uid == null) {
            Log.e("SplashScreen", "User ID from notification is null");
            navigateBasedOnUserRole();
            return;
        }

        // Fetch user details from Firestore
        FireBaseUtil.allUserCollectionReference().document(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                UserModel model = task.getResult().toObject(UserModel.class);
                if (model != null) {
                    // Navigate to Chats and ChatActivity
                    Intent im = new Intent(this, Chats.class);
                    im.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(im);

                    Intent i = new Intent(this, ChatActivity.class);
                    AndroidUtils.passModelAsIntent(i, model);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                } else {
                    Log.e("SplashScreen", "UserModel is null");
                    navigateBasedOnUserRole();
                }
            } else {
                Log.e("SplashScreen", "Firestore query failed", task.getException());
                navigateBasedOnUserRole();
            }
        });
    }
}