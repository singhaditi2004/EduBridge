package com.example.edubridge.Model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@IgnoreExtraProperties
public class Job {
    private String title;
    private String description;
    private String subject;
    private String postedBy;

    @Exclude
    private String key;

    private long timestamp;
    private Map<String, Boolean> searchKeywords = new HashMap<>();

    // Required empty constructor for Firebase
    public Job() {
        // Initialize default values
        this.searchKeywords = new HashMap<>();
    }

    public Job(String title, String description, String subject, String postedBy, long timestamp) {
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.postedBy = postedBy;
        this.timestamp = timestamp;
        this.searchKeywords = new HashMap<>();
    }

    // Getters and setters with PropertyName annotations
    @PropertyName("title")
    public String getTitle() {
        return title;
    }

    @PropertyName("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @PropertyName("description")
    public String getDescription() {
        return description;
    }

    @PropertyName("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @PropertyName("subject")
    public String getSubject() {
        return subject;
    }

    @PropertyName("subject")
    public void setSubject(String subject) {
        this.subject = subject;
    }

    @PropertyName("postedBy")
    public String getPostedBy() {
        return postedBy;
    }

    @PropertyName("postedBy")
    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    @PropertyName("timestamp")
    public long getTimestamp() {
        return timestamp;
    }

    @PropertyName("timestamp")
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @PropertyName("searchKeywords")
    public Map<String, Boolean> getSearchKeywords() {
        return searchKeywords;
    }

    @PropertyName("searchKeywords")
    public void setSearchKeywords(Map<String, Boolean> searchKeywords) {
        this.searchKeywords = searchKeywords != null ? searchKeywords : new HashMap<>();
    }

    // Add this if not already present
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Job job = (Job) o;
        return timestamp == job.timestamp &&
                Objects.equals(title, job.title) &&
                Objects.equals(description, job.description) &&
                Objects.equals(subject, job.subject) &&
                Objects.equals(postedBy, job.postedBy);
    }
    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }
    public void setKeyFromSnapshot(DataSnapshot snapshot) {
        this.key = snapshot.getKey();
    }
    @Override
    public int hashCode() {
        return Objects.hash(title, description, subject, postedBy, timestamp);
    }
}