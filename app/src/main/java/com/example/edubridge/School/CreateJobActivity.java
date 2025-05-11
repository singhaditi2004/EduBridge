package com.example.edubridge.School;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.edubridge.Model.Job;
import com.example.edubridge.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class CreateJobActivity extends AppCompatActivity {

    private TextInputEditText etJobTitle, etDescription, etSubject;
    private Button btnPostJob;
    private View loadingOverlay;
    private FirebaseAuth mAuth;
    private DatabaseReference jobsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_job);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        jobsRef = FirebaseDatabase.getInstance().getReference().child("Jobs");

        // Initialize views
        etJobTitle = findViewById(R.id.et_job_title);
        etDescription = findViewById(R.id.et_description);
        etSubject = findViewById(R.id.et_subject);
        btnPostJob = findViewById(R.id.btn_post_job);
        loadingOverlay = findViewById(R.id.loading_overlay);

        btnPostJob.setOnClickListener(v -> postJob());
    }

    private void postJob() {
        // Get input values
        String title = etJobTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String subject = etSubject.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(title)) {
            etJobTitle.setError("Job title is required");
            etJobTitle.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(subject)) {
            etSubject.setError("Subject is required");
            etSubject.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            etDescription.setError("Description is required");
            etDescription.requestFocus();
            return;
        }

        // Show loading indicator
        loadingOverlay.setVisibility(View.VISIBLE);
        btnPostJob.setEnabled(false);

        // Get current user ID
        String schoolId = mAuth.getCurrentUser().getUid();
        String jobId = jobsRef.push().getKey();

        // Create job object
        Job job = new Job(
                title,
                description,
                subject,
                schoolId,
                System.currentTimeMillis()
        );

        // Add search keywords for better searching
        job.setSearchKeywords(generateSearchKeywords(title + " " + subject));

        // Save to Firebase
        jobsRef.child(jobId).setValue(job)
                .addOnCompleteListener(task -> {
                    // Hide loading indicator
                    loadingOverlay.setVisibility(View.GONE);
                    btnPostJob.setEnabled(true);

                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Job posted successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Close activity after successful post
                    } else {
                        Toast.makeText(this,
                                "Failed to post job: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private Map<String, Boolean> generateSearchKeywords(String text) {
        Map<String, Boolean> keywords = new HashMap<>();
        String[] words = text.toLowerCase().split("\\s+");
        for (String word : words) {
            if (word.length() > 2) { // Only include words longer than 2 characters
                keywords.put(word, true);
            }
        }
        return keywords;
    }
}