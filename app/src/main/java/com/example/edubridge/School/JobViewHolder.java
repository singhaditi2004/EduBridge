package com.example.edubridge.School;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edubridge.R;
import com.google.android.material.button.MaterialButton;

public class JobViewHolder extends RecyclerView.ViewHolder {
    public TextView tvJobTitle, tvSubject, tvDescription, tvApplicantCount, tvPostedDate, tvJobStatus;
    public MaterialButton btnViewApplicants, btnEditJob, btnDeleteJob;

    public JobViewHolder(@NonNull View itemView) {
        super(itemView);
        tvJobTitle = itemView.findViewById(R.id.tv_job_title);
        tvSubject = itemView.findViewById(R.id.tv_subject);
        tvDescription = itemView.findViewById(R.id.tv_description);
        tvApplicantCount = itemView.findViewById(R.id.tv_applicant_count);
        tvPostedDate = itemView.findViewById(R.id.tv_posted_date);
        tvJobStatus = itemView.findViewById(R.id.tv_job_status);

        btnViewApplicants = itemView.findViewById(R.id.btn_view_applicants);
        btnEditJob = itemView.findViewById(R.id.btn_edit_job);
        btnDeleteJob = itemView.findViewById(R.id.btn_delete_job);

        // Debugging checks
        if (tvPostedDate == null) throw new RuntimeException("tv_posted_date not found");
        if (btnViewApplicants == null) throw new RuntimeException("btn_view_applicants not found");
    }

    // ... keep all your existing setter methods ...

    public void setJobTitle(String title) {
        tvJobTitle.setText(title);
    }

    public void setSubject(String subject) {
        tvSubject.setText(subject);
        // Set background color based on subject if needed
        tvSubject.setBackgroundResource(getSubjectColor(subject));
    }

    public void setDescription(String description) {
        tvDescription.setText(description);
    }

    public void setApplicantCount(String count) {
        tvApplicantCount.setText(count);
    }

    public void setPostedDate(String date) {
        tvPostedDate.setText(date);
    }

    public void setJobStatus(String status) {
        tvJobStatus.setText(status);
        // Set different background colors based on status
        switch (status.toLowerCase()) {
            case "active":
                tvJobStatus.setBackgroundResource(R.drawable.status_background_active);
                break;
            case "closed":
                tvJobStatus.setBackgroundResource(R.drawable.status_background_closed);
                break;
            default:
                tvJobStatus.setBackgroundResource(R.drawable.status_background_default);
        }
    }

    private int getSubjectColor(String subject) {
        switch (subject.toLowerCase()) {
            case "mathematics": return R.drawable.bg_rounded_maths;
            case "science": return R.drawable.bg_rounded_science;
            case "language": return R.drawable.bg_rounded_language;
            case "sports": return R.drawable.bg_rounded_sports;
            default: return R.drawable.bg_rounded_subject;
        }
    }
}