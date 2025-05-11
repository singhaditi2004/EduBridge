package com.example.edubridge.School;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.edubridge.Model.Job;
import com.example.edubridge.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ViewApplicantsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout emptyStateView;
    private FrameLayout loadingOverlay;
    private FloatingActionButton fabAddJob;

    private FirebaseAuth mAuth;
    private DatabaseReference jobsRef;

    private FirebaseRecyclerAdapter<Job, JobViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_applicants);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        jobsRef = FirebaseDatabase.getInstance().getReference().child("Jobs");

        // Initialize views
        recyclerView = findViewById(R.id.recycler_view_jobs);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        emptyStateView = findViewById(R.id.empty_state);
        loadingOverlay = findViewById(R.id.loading_overlay);
        fabAddJob = findViewById(R.id.fab_add_job);

        // Setup RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Setup swipe to refresh
        swipeRefreshLayout.setOnRefreshListener(this::loadJobs);

        // FAB click listener
        fabAddJob.setOnClickListener(v -> {
            startActivity(new Intent(ViewApplicantsActivity.this, CreateJobActivity.class));
        });

        // Button in empty state
        findViewById(R.id.btn_create_job).setOnClickListener(v -> {
            startActivity(new Intent(ViewApplicantsActivity.this, CreateJobActivity.class));
        });

        // Load jobs
        loadJobs();
    }

    private void loadJobs() {
        loadingOverlay.setVisibility(View.VISIBLE);
        String schoolId = mAuth.getCurrentUser().getUid();
        Query query = jobsRef.orderByChild("postedBy").equalTo(schoolId);

        FirebaseRecyclerOptions<Job> options = new FirebaseRecyclerOptions.Builder<Job>()
                .setQuery(query, Job.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Job, JobViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull JobViewHolder holder, int position, @NonNull Job model) {
                // Ensure model has a key
                if (model.getKey() == null) {
                    Log.e("Adapter", "Job key is null at position: " + position);
                    return;
                }
                // Set job data to views
                holder.setJobTitle(model.getTitle());
                holder.setSubject(model.getSubject());
                holder.setDescription(model.getDescription());

                // Format timestamp to relative time
                String timeAgo = android.text.format.DateUtils.getRelativeTimeSpanString(
                        model.getTimestamp(),
                        System.currentTimeMillis(),
                        android.text.format.DateUtils.MINUTE_IN_MILLIS).toString();
                holder.setPostedDate("Posted " + timeAgo);

                // Set applicant count
                setApplicantCount(holder, model.getKey());

                // Set default status
                holder.setJobStatus("active");

                // View Applicants button click
                holder.btnViewApplicants.setOnClickListener(v -> {
                    Intent intent = new Intent(ViewApplicantsActivity.this, ViewJobApplicant.class);
                    intent.putExtra("jobId", model.getKey());
                    startActivity(intent);
                });

                // Edit button click
                holder.btnEditJob.setOnClickListener(v -> {
                    // Implement edit functionality
                    Toast.makeText(ViewApplicantsActivity.this, "Edit job", Toast.LENGTH_SHORT).show();
                });

                // Delete button click
                holder.btnDeleteJob.setOnClickListener(v -> {
                    deleteJob(model.getKey());
                });
            }

            @NonNull
            @Override
            public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_job_card, parent, false);
                return new JobViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                loadingOverlay.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

                if (getItemCount() == 0) {
                    emptyStateView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    emptyStateView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void setApplicantCount(JobViewHolder holder, String jobId) {
        if (jobId == null || jobId.isEmpty()) {
            holder.setApplicantCount("0 Applicants");
            return;
        }
        DatabaseReference applicantsRef = FirebaseDatabase.getInstance().getReference()
                .child("Applicants").child(jobId);

        applicantsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count = snapshot.getChildrenCount();
                holder.setApplicantCount(count + " Applicants");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                holder.setApplicantCount("0 Applicants");
            }
        });
    }

    private void deleteJob(String jobId) {
        loadingOverlay.setVisibility(View.VISIBLE);
        jobsRef.child(jobId).removeValue()
                .addOnCompleteListener(task -> {
                    loadingOverlay.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(ViewApplicantsActivity.this, "Job deleted successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ViewApplicantsActivity.this, "Failed to delete job", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}