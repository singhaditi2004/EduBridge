package com.example.edubridge;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.edubridge.Teacher.TeacherHome;
import com.example.edubridge.School.SchoolHome; // You'll need to create this
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class GetStarted extends AppCompatActivity {
    private AppCompatButton signIn, signUp;
    private MaterialButton contGoogle;
    private EditText passLog, emailLog;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_get_started);

        // Initialize views
        signIn = findViewById(R.id.loginBtn);
        signUp = findViewById(R.id.signUpBtn);
        contGoogle = findViewById(R.id.contWithGoogle);
        passLog = findViewById(R.id.passwLog);
        emailLog = findViewById(R.id.emailLog);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        signIn.setOnClickListener(v -> handleLogin());
        signUp.setOnClickListener(v -> {
            startActivity(new Intent(GetStarted.this, SignUp.class));
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void handleLogin() {
        String email = emailLog.getText().toString().trim();
        String password = passLog.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Email can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Password can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading indicator if you have one

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkUserRole(user.getUid());
                        }
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(GetStarted.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserRole(String uid) {
        // First check if user is a teacher
        db.collection("teachers").document(uid).get()
                .addOnCompleteListener(teacherTask -> {
                    if (teacherTask.isSuccessful() && teacherTask.getResult().exists()) {
                        // User is a teacher
                        startActivity(new Intent(this, TeacherHome.class));
                        finish();
                    } else {
                        // If not teacher, check if school
                        checkSchoolRole(uid);
                    }
                });
    }

    private void checkSchoolRole(String uid) {
        db.collection("schools").document(uid).get()
                .addOnCompleteListener(schoolTask -> {
                    if (schoolTask.isSuccessful() && schoolTask.getResult().exists()) {
                        // User is a school
                        startActivity(new Intent(this, SchoolHome.class));
                        finish();
                    } else {
                        // User not found in either collection
                        Toast.makeText(this, "User role not found. Please complete your profile.",
                                Toast.LENGTH_SHORT).show();
                        // Optionally redirect to profile completion
                        FirebaseAuth.getInstance().signOut();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is already logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            checkUserRole(currentUser.getUid());
        }
    }
}