package com.example.edubridge.Teacher;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edubridge.Model.Job;
import com.example.edubridge.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Jobs extends Fragment {

    private ImageButton searchImgButt;
    private SearchView searchView;
    private RecyclerView recycleJobs;
    private FirebaseRecyclerAdapter<Job, TeacherJobViewHolder> adapter;
    private DatabaseReference jobsRef, applicantsRef;
    private FirebaseAuth mAuth;

    public Jobs() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_jobs, container, false);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        jobsRef = FirebaseDatabase.getInstance().getReference().child("Jobs");
        applicantsRef = FirebaseDatabase.getInstance().getReference().child("Applicants");

        // Initialize views
        searchImgButt = view.findViewById(R.id.searchImgButt);
        searchView = view.findViewById(R.id.searchView);
        recycleJobs = view.findViewById(R.id.recycleJobs);

        // Setup RecyclerView
        // Add this in onCreateView()
       /* recycleJobs.setLayoutManager(new LinearLayoutManager(getContext()));
        recycleJobs.setHasFixedSize(true);
        recycleJobs.setItemAnimator(null); // Disable animations for stability*/
        // In your onCreateView:
        recycleJobs.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public void onLayoutCompleted(RecyclerView.State state) {
                super.onLayoutCompleted(state);
                Log.d("RECYCLER_VIEW", "Layout completed");
            }
        });

// Add item decoration for spacing
        recycleJobs.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL) {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = 16; // 16dp bottom margin
            }
        });

        // Initialize category buttons
        ImageView mathsJob = view.findViewById(R.id.mathsJob);
        ImageView scienceJob = view.findViewById(R.id.scienceJob);
        ImageView languageJob = view.findViewById(R.id.languageJob);
        ImageView specialJob = view.findViewById(R.id.specialJob);

        // Set click listeners for category buttons
        mathsJob.setOnClickListener(v -> filterJobsByCategory("Mathematics"));
        scienceJob.setOnClickListener(v -> filterJobsByCategory("Science"));
        languageJob.setOnClickListener(v -> filterJobsByCategory("Language"));
        specialJob.setOnClickListener(v -> filterJobsByCategory("Special Education"));

        // Set search functionality
        setupSearch();

        loadAllJobs();

        return view;
    }

    private void filterJobsByCategory(String category) {
        Query query = jobsRef.orderByChild("subject").equalTo(category);

        FirebaseRecyclerOptions<Job> options = new FirebaseRecyclerOptions.Builder<Job>()
                .setQuery(query, Job.class)
                .build();

        adapter.updateOptions(options);
    }

    private void loadAllJobs() {
        // Remove any existing listeners
        if (adapter != null) {
            adapter.stopListening();
        }

        // Create query - order by timestamp descending (newest first)
        Query query = jobsRef.orderByChild("timestamp");

        FirebaseRecyclerOptions<Job> options = new FirebaseRecyclerOptions.Builder<Job>()
                .setQuery(query, Job.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Job, TeacherJobViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TeacherJobViewHolder holder, int position, @NonNull Job model) {
                // Get the key from Firebase
                String jobKey = getRef(position).getKey();
                model.setKey(jobKey); // Set the key in the model

                Log.d("JOB_BIND", "Binding job: " + model.getTitle() + " | Key: " + jobKey);

                // Set all text fields
                holder.setJobTitle(model.getTitle() != null ? model.getTitle() : "No Title");
                holder.setSubject(model.getSubject() != null ? model.getSubject() : "No Subject");
                holder.setDescription(model.getDescription() != null ? model.getDescription() : "No Description");
                holder.setSchoolName(model.getPostedBy() != null ? model.getPostedBy() : "Unknown School");

                // Convert and set timestamp
                if (model.getTimestamp() > 0) {
                    String timeAgo = DateUtils.getRelativeTimeSpanString(
                            model.getTimestamp(),
                            System.currentTimeMillis(),
                            DateUtils.MINUTE_IN_MILLIS
                    ).toString();
                    holder.setPostedDate("Posted " + timeAgo);
                } else {
                    holder.setPostedDate("Posted date unknown");
                }

                // Initialize apply button state
                checkApplicationStatus(holder, model.getKey());

                // Set click listener
                holder.btnApply.setOnClickListener(v -> {
                    if (holder.btnApply.isEnabled()) {
                        applyForJob(model.getKey());
                    }
                });
                if (jobKey != null && !jobKey.isEmpty()) {
                    checkApplicationStatus(holder, jobKey);
                } else {
                    Log.e("JOB_ERROR", "Job key is null for position: " + position);
                    holder.setAppliedStatus(false);
                }
            }

            @NonNull
            @Override
            public TeacherJobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_teacher_job_card, parent, false);
                return new TeacherJobViewHolder(view);
            }
        };

        // Set and start adapter
        recycleJobs.setAdapter(adapter);
        adapter.startListening();

        // Now load real data
        jobsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("FIREBASE_DEBUG", "Total jobs: " + snapshot.getChildrenCount());
                for (DataSnapshot child : snapshot.getChildren()) {
                    Log.d("FIREBASE_DEBUG", "Job: " + child.getValue(Job.class).getTitle());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("LOAD_ERROR", error.getMessage());
            }
        });
    }
    private void checkApplicationStatus(TeacherJobViewHolder holder, String jobId) {
        Log.d("three","checkinhgggggv");
        String teacherId = mAuth.getCurrentUser().getUid();

        applicantsRef.child(jobId).child(teacherId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    holder.setAppliedStatus(true);
                } else {
                    holder.setAppliedStatus(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                holder.setAppliedStatus(false);
            }
        });
    }

    private void applyForJob(String jobId) {
        String teacherId = mAuth.getCurrentUser().getUid();
        HashMap<String, Object> application = new HashMap<>();
        application.put("appliedAt", System.currentTimeMillis());
        application.put("status", "pending");

        applicantsRef.child(jobId).child(teacherId).updateChildren(application)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Application submitted successfully", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Failed to submit application", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private Map<String, Boolean> generateSearchKeywords(String text) {
        Map<String, Boolean> keywords = new HashMap<>();
        String[] words = text.toLowerCase().split("\\s+");
        for (String word : words) {
            if (word.length() > 2) {
                keywords.put(word, true);
            }
        }
        return keywords;
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchJobs(newText.toLowerCase().trim());
                return true;
            }
        });
    }

    private void searchJobs(String searchText) {
        Query query;

        if (searchText.isEmpty()) {
            query = jobsRef.orderByChild("timestamp");
        } else {
            query = jobsRef.orderByChild("searchKeywords")
                    .startAt(searchText)
                    .endAt(searchText + "\uf8ff");
        }

        FirebaseRecyclerOptions<Job> newOptions = new FirebaseRecyclerOptions.Builder<Job>()
                .setQuery(query, Job.class)
                .build();

        adapter.updateOptions(newOptions);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            Log.d("five","checkinhgggggv");
            adapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}