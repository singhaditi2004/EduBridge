package com.example.edubridge;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashScreen extends AppCompatActivity {
    private static final int SPLASH_DISPLAY_LENGTH = 2000;

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
        new Handler().postDelayed(() -> {
            // After the splash screen duration, start the main activity
            if(FireBaseUtil.isLoogedIn()){
                Intent mainIntent = new Intent(SplashScreen.this, TeacherHome.class);
                startActivity(mainIntent);
            }
            Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        }, SPLASH_DISPLAY_LENGTH);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}