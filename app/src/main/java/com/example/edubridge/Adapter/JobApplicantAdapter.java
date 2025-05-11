package com.example.edubridge.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edubridge.Model.Applicant;
import com.example.edubridge.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class JobApplicantAdapter extends RecyclerView.Adapter<JobApplicantAdapter.ApplicantViewHolder> {

    private List<Applicant> applicantList;

    public JobApplicantAdapter(List<Applicant> applicantList) {
        this.applicantList = applicantList;
    }

    @NonNull
    @Override
    public ApplicantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_applicant, parent, false);
        return new ApplicantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicantViewHolder holder, int position) {
        Applicant applicant = applicantList.get(position);

        // Set applicant data to views
        holder.tvName.setText(applicant.getName());
        holder.tvQualification.setText(applicant.getQualification());
        holder.tvApplicationNote.setText(applicant.getApplicationNote());

        // Format timestamp to relative time
        String timeAgo = getTimeAgo(applicant.getTimestamp());
        holder.tvApplicationDate.setText("Applied " + timeAgo);

        // Set status
        holder.tvStatus.setText(applicant.getStatus());
        // You can set different background colors based on status
        switch (applicant.getStatus().toLowerCase()) {
            case "accepted":
                holder.tvStatus.setBackgroundResource(R.drawable.status_background_accepted);
                break;
            case "rejected":
                holder.tvStatus.setBackgroundResource(R.drawable.status_background_rejected);
                break;
            default:
                holder.tvStatus.setBackgroundResource(R.drawable.status_background_pending);
        }

        // Set click listeners for buttons
        holder.btnViewProfile.setOnClickListener(v -> {
            // Implement view profile functionality
        });

        holder.btnContact.setOnClickListener(v -> {
            // Implement contact functionality
        });
    }

    @Override
    public int getItemCount() {
        return applicantList.size();
    }

    private String getTimeAgo(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;

        if (diff < 60000) { // Less than 1 minute
            return "just now";
        } else if (diff < 3600000) { // Less than 1 hour
            long minutes = diff / 60000;
            return minutes + " min ago";
        } else if (diff < 86400000) { // Less than 1 day
            long hours = diff / 3600000;
            return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            return sdf.format(new Date(timestamp));
        }
    }

    static class ApplicantViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView ivApplicantPhoto;
        TextView tvName, tvQualification, tvApplicationDate, tvApplicationNote, tvStatus;
        MaterialButton btnViewProfile, btnContact, btnStatusMenu;

        public ApplicantViewHolder(@NonNull View itemView) {
            super(itemView);
            ivApplicantPhoto = itemView.findViewById(R.id.iv_applicant_photo);
            tvName = itemView.findViewById(R.id.tv_applicant_name);
            tvQualification = itemView.findViewById(R.id.tv_applicant_qualification);
            tvApplicationDate = itemView.findViewById(R.id.tv_application_date);
            tvApplicationNote = itemView.findViewById(R.id.tv_application_note);
            tvStatus = itemView.findViewById(R.id.tv_application_status);
            btnViewProfile = itemView.findViewById(R.id.btn_view_profile);
            btnContact = itemView.findViewById(R.id.btn_contact);
            btnStatusMenu = itemView.findViewById(R.id.btn_status_menu);
        }
    }
}