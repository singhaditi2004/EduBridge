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

import com.example.edubridge.Chat.ChatActivity;
import com.example.edubridge.Chat.Chats;
import com.example.edubridge.Model.UserModel;
import com.example.edubridge.Teacher.TeacherHome;
import com.example.edubridge.Utils.AndroidUtils;
import com.example.edubridge.Utils.FireBaseUtil;

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

        if (getIntent().getExtras() != null) {
            //from notif
            String uid = getIntent().getExtras().getString("userId");
            FireBaseUtil.allUserCollectionReference().document(uid).get().addOnCompleteListener(task -> {
                if (FireBaseUtil.isLoogedIn() && task.isSuccessful()) {
                    UserModel model = task.getResult().toObject(UserModel.class);
                    Intent im = new Intent(this, Chats.class);
                    im.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(im);
                    Intent i = new Intent(this, ChatActivity.class);
                    AndroidUtils.passModelAsIntent(i, model);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                }
            });
        } else {
            new Handler().postDelayed(() -> {
                // After the splash screen duration, start the main activity
                Intent mainIntent;
                if (FireBaseUtil.isLoogedIn()) {
                    mainIntent = new Intent(SplashScreen.this, TeacherHome.class);
                } else {
                    mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                }
                startActivity(mainIntent);
                finish();

            }, SPLASH_DISPLAY_LENGTH);
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}