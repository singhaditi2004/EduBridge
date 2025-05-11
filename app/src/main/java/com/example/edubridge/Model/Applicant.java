package com.example.edubridge.Model;

public class Applicant {
    private String id;
    private String name;
    private String email;
    private String qualification;
    private String experience;
    private String applicationNote;
    private long timestamp;
    private String status; // "pending", "reviewed", "accepted", "rejected"

    // Required empty constructor for Firebase
    public Applicant() {
    }

    public Applicant(String name, String email, String qualification, String experience,
                     String applicationNote, long timestamp, String status) {
        this.name = name;
        this.email = email;
        this.qualification = qualification;
        this.experience = experience;
        this.applicationNote = applicationNote;
        this.timestamp = timestamp;
        this.status = status;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getApplicationNote() {
        return applicationNote;
    }

    public void setApplicationNote(String applicationNote) {
        this.applicationNote = applicationNote;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}