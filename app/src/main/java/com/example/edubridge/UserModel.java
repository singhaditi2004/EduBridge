package com.example.edubridge;

public class UserModel {
    private String userId;
    private String name;
    private String email;
    private String role;
    private String profileImageUrl;
    private String location;

    // Default constructor required for calls to DataSnapshot.getValue(UserModel.class)
    public UserModel() {
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public UserModel(String userId, String name, String email, String role, String profileImageUrl, String location) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.profileImageUrl = profileImageUrl;
        this.location = location;
    }

    // Getters and setters for each field
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                '}';
    }
}
