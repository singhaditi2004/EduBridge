package com.example.edubridge.Teacher;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edubridge.R;

public class TeacherJobViewHolder extends RecyclerView.ViewHolder {

    public TextView tvJobTitle, tvSubject, tvDescription, tvSchoolName, tvPostedDate;
    public Button btnApply;

    public TeacherJobViewHolder(@NonNull View itemView) {
        super(itemView);

        tvJobTitle = itemView.findViewById(R.id.tv_job_title);
        tvSubject = itemView.findViewById(R.id.tv_subject);
        tvDescription = itemView.findViewById(R.id.tv_description);
        tvSchoolName = itemView.findViewById(R.id.tv_school_name);
        tvPostedDate = itemView.findViewById(R.id.tv_posted_date);
        btnApply = itemView.findViewById(R.id.btn_apply);
    }

    public void setJobTitle(String title) {
        tvJobTitle.setText(title);
    }

    public void setSubject(String subject) {
        tvSubject.setText("Subject: " + subject);
    }

    public void setDescription(String description) {
        tvDescription.setText(description);
    }

    public void setSchoolName(String schoolName) {
        tvSchoolName.setText("School: " + schoolName);
    }

    public void setPostedDate(String date) {
        tvPostedDate.setText(date);
    }

    public void setAppliedStatus(boolean hasApplied) {
        if (hasApplied) {
            btnApply.setText("Applied");
            btnApply.setEnabled(false);
            btnApply.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.sky));
        } else {
            btnApply.setText("Apply");
            btnApply.setEnabled(true);
            btnApply.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.sky));
        }
    }
}