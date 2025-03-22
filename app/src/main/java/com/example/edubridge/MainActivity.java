package com.example.edubridge;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.edubridge.Utils.FireBaseUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.firestore.DocumentReference;

public class MainActivity extends AppCompatActivity {
    AppCompatButton getBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        getBtn = findViewById(R.id.getBtn);
        getBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GetStarted.class);
                startActivity(intent);
            }
        });
        getFCMToken();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    void getFCMToken() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String token = task.getResult();
                    DocumentReference userRef = FireBaseUtil.currentUserDetails();
                    if (userRef != null) {
                        userRef.update("FCMToken", token)
                                .addOnSuccessListener(aVoid -> Log.d("FCM", "Token updated successfully"))
                                .addOnFailureListener(e -> Log.e("FCM", "Failed to update token", e));
                    } else {
                        Log.e("FCM", "User document reference is null");
                    }
                } else {
                    Log.e("FCM", "Failed to get FCM token");
                }
            });
        } else {
            Log.e("FCM", "User not logged in. Skipping token update.");
        }
    }
}
