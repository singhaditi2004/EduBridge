package com.example.edubridge.School;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.edubridge.Model.Job;
import com.example.edubridge.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class school_jobs extends Fragment {

    private ImageButton btnAddJob, btnApplicants, btnBack;
    private TextInputEditText etSearch;
    private RecyclerView rvTeachers;
    private FirebaseRecyclerAdapter<Job, JobViewHolder> adapter;
    private DatabaseReference jobsRef;
    private FirebaseAuth mAuth;


    public school_jobs() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_school_jobs, container, false);

        // Initialize views and Firebase
        mAuth = FirebaseAuth.getInstance();
        jobsRef = FirebaseDatabase.getInstance().getReference().child("Jobs");

        btnBack = view.findViewById(R.id.btn_back);
        btnAddJob = view.findViewById(R.id.btn_add_job);
        btnApplicants = view.findViewById(R.id.btn_applicants);
        etSearch = view.findViewById(R.id.et_search);
        rvTeachers = view.findViewById(R.id.rv_teachers);

        // Configure RecyclerView
        rvTeachers.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvTeachers.setLayoutManager(layoutManager);

        // Set click listeners
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        btnAddJob.setOnClickListener(v -> openCreateJobActivity());
        btnApplicants.setOnClickListener(v -> openViewApplicantsActivity());

        setupCategoryButtons(view);
        loadSchoolJobs();
        setupSearch();

        return view;
    }


    private void setupCategoryButtons(View view) {
        view.findViewById(R.id.card_maths).setOnClickListener(v -> filterJobsByCategory("Mathematics"));
        view.findViewById(R.id.card_science).setOnClickListener(v -> filterJobsByCategory("Science"));
        view.findViewById(R.id.card_language).setOnClickListener(v -> filterJobsByCategory("Language"));
        view.findViewById(R.id.card_sport).setOnClickListener(v -> filterJobsByCategory("Sports"));
    }

    private void filterJobsByCategory(String category) {
        String schoolId = mAuth.getCurrentUser().getUid();
        Query query = jobsRef.orderByChild("postedBy").equalTo(schoolId)
                .orderByChild("subject").equalTo(category);

        FirebaseRecyclerOptions<Job> options = new FirebaseRecyclerOptions.Builder<Job>()
                .setQuery(query, Job.class)
                .build();

        adapter.updateOptions(options);
    }
    private void setApplicantCount(JobViewHolder holder, String jobId) {
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

    private void loadSchoolJobs() {
        String schoolId = mAuth.getCurrentUser().getUid();
        Query query = jobsRef;
        FirebaseRecyclerOptions<Job> options = new FirebaseRecyclerOptions.Builder<Job>()
                .setQuery(query, snapshot -> {
                    Job job = snapshot.getValue(Job.class);
                    if (job != null) {
                        job.setKey(snapshot.getKey());
                        job.setSearchKeywords(generateSearchKeywords(job.getTitle() + " " + job.getSubject()));
                    }
                    return job;
                })
                .build();

        // Clear old adapter if exists
        if (adapter != null) {
            adapter.stopListening();
        }

        adapter = new FirebaseRecyclerAdapter<Job, JobViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull JobViewHolder holder, int position, @NonNull Job model) {
                try {
                    holder.setJobTitle(model.getTitle());
                    holder.setDescription(model.getDescription());
                    holder.setSubject(model.getSubject());

                    String timeAgo = DateUtils.getRelativeTimeSpanString(
                            model.getTimestamp(),
                            System.currentTimeMillis(),
                            DateUtils.MINUTE_IN_MILLIS).toString();
                    holder.setPostedDate("Posted " + timeAgo);

                    // Set default status
                    holder.setJobStatus("active");

                    // Set applicant count (you'll need to implement this)
                    setApplicantCount(holder, model.getKey());

                    holder.btnViewApplicants.setOnClickListener(v -> {
                        openJobApplicantsActivity(model.getKey());
                    });

                    holder.btnEditJob.setOnClickListener(v -> {
                        // Handle edit
                    });

                    holder.btnDeleteJob.setOnClickListener(v -> {
                      //  deleteJob(model.getKey());
                        Toast.makeText(getContext(), "Deleted job", Toast.LENGTH_SHORT).show();
                    });
                } catch (Exception e) {
                    Log.e("JobAdapter", "Error binding view", e);
                }
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
                // Handle empty state if needed
            }
        };

        // Important configuration
        adapter.setHasStableIds(true);  // Crucial for preventing inconsistencies
        rvTeachers.setAdapter(adapter);
        adapter.startListening();
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

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = s.toString().toLowerCase().trim();
                searchJobs(searchText);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void searchJobs(String searchText) {
        String schoolId = mAuth.getCurrentUser().getUid();
        Query query;

        if (searchText.isEmpty()) {
            query = jobsRef.orderByChild("postedBy").equalTo(schoolId);
        } else {
            query = jobsRef.orderByChild("searchKeywords")
                    .startAt(searchText)
                    .endAt(searchText + "\uf8ff")
                    .orderByChild("postedBy").equalTo(schoolId);
        }

        // Create new options
        FirebaseRecyclerOptions<Job> newOptions = new FirebaseRecyclerOptions.Builder<Job>()
                .setQuery(query, Job.class)
                .build();

        // Update adapter options
        adapter.updateOptions(newOptions);
    }

    private void openCreateJobActivity() {
        startActivity(new Intent(getActivity(), CreateJobActivity.class));
    }

    private void openViewApplicantsActivity() {
        startActivity(new Intent(getActivity(), ViewApplicantsActivity.class));
    }

    private void openJobApplicantsActivity(String jobId) {
        Intent intent = new Intent(getActivity(), ViewApplicantsActivity.class);
        intent.putExtra("jobId", jobId);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
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