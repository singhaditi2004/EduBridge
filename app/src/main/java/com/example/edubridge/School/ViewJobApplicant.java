package com.example.edubridge.School;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edubridge.Adapter.JobApplicantAdapter;
import com.example.edubridge.Model.Applicant;
import com.example.edubridge.Model.Job;
import com.example.edubridge.R;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewJobApplicant extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvJobTitleHeader, tvSubjectHeader, tvTotalApplicants;
    private RecyclerView recyclerViewApplicants;
    private LinearLayout emptyStateView;
    private FrameLayout loadingOverlay;
    private Chip chipFilter;

    private String jobId;
    private JobApplicantAdapter adapter;
    private List<Applicant> applicantList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_job_applicant);

        // Get job ID from intent
        jobId = getIntent().getStringExtra("jobId");
        if (jobId == null) {
            Toast.makeText(this, "Job ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        initViews();

        // Setup toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Setup RecyclerView
        setupRecyclerView();

        // Load job details and applicants
        loadJobDetails();
        loadApplicants();

        // Filter chip click listener
        chipFilter.setOnClickListener(v -> showFilterOptions());
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvJobTitleHeader = findViewById(R.id.tv_job_title_header);
        tvSubjectHeader = findViewById(R.id.tv_subject_header);
        tvTotalApplicants = findViewById(R.id.tv_total_applicants);
        recyclerViewApplicants = findViewById(R.id.recycler_view_applicants);
        emptyStateView = findViewById(R.id.empty_state);
        loadingOverlay = findViewById(R.id.loading_overlay);
        chipFilter = findViewById(R.id.chip_filter);
    }

    private void setupRecyclerView() {
        adapter = new JobApplicantAdapter(applicantList);
        recyclerViewApplicants.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewApplicants.setAdapter(adapter);
    }

    private void loadJobDetails() {
        DatabaseReference jobRef = FirebaseDatabase.getInstance().getReference("Jobs").child(jobId);
        jobRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Job job = snapshot.getValue(Job.class);
                if (job != null) {
                    tvJobTitleHeader.setText(job.getTitle());
                    tvSubjectHeader.setText("Subject: " + job.getSubject());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewJobApplicant.this, "Failed to load job details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadApplicants() {
        loadingOverlay.setVisibility(View.VISIBLE);
        DatabaseReference applicantsRef = FirebaseDatabase.getInstance().getReference("Applicants").child(jobId);

        applicantsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                applicantList.clear();
                for (DataSnapshot applicantSnapshot : snapshot.getChildren()) {
                    Applicant applicant = applicantSnapshot.getValue(Applicant.class);
                    if (applicant != null) {
                        applicant.setId(applicantSnapshot.getKey());
                        applicantList.add(applicant);
                    }
                }

                loadingOverlay.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();

                // Update applicant count
                tvTotalApplicants.setText(applicantList.size() + " Applicants");

                // Show empty state if no applicants
                if (applicantList.isEmpty()) {
                    emptyStateView.setVisibility(View.VISIBLE);
                    recyclerViewApplicants.setVisibility(View.GONE);
                } else {
                    emptyStateView.setVisibility(View.GONE);
                    recyclerViewApplicants.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingOverlay.setVisibility(View.GONE);
                Toast.makeText(ViewJobApplicant.this, "Failed to load applicants", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showFilterOptions() {
        // Implement filter options dialog
        Toast.makeText(this, "Filter options will be shown here", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up any listeners if needed
    }
}