package com.example.edubridge.Model;

import android.util.Log;

import com.example.edubridge.Utils.FireBaseUtil;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class UserModel {
    private String userId;
    private String name;
    private String email;
    private String role;
    private String profileImageUrl;
    private String location;
    private String FCMToken;
    public UserModel(String userId, String name, String email, String role, String profileImageUrl, String location, String phone) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.profileImageUrl = profileImageUrl;
        this.location = location;
        this.phone = phone;
    }

    public String getFCMToken() {
        return FCMToken;
    }

    public void setFCMToken(String FCMToken) {
        this.FCMToken = FCMToken;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String phone;

    // Default constructor required for calls to DataSnapshot.getValue(UserModel.class)
    public UserModel() {
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
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

    public String getFcmToken() {
        final String[] token = new String[1];
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        // Log and handle the failure
                        Log.e("FCM Token", "Fetching FCM token failed", task.getException());
                        return;
                    }

                    // Get the token
                     token[0] = task.getResult();
                    Log.d("FCM Token", "Token: " + token[0]);

                    // Store the token in your backend (Firebase Firestore/Realtime Database or SharedPreferences)
                    saveFcmTokenToBackend(token[0]);
                });
        return token[0];

    }

    // Helper method to save the token to your backend
    private void saveFcmTokenToBackend(String token) {
        String userId = FireBaseUtil.getCurrentUserId(); // Ensure this returns the logged-in user's ID
        if (userId != null) {
            FirebaseFirestore.getInstance().collection("users")
                    .document(userId)
                    .update("deviceToken", token)
                    .addOnSuccessListener(aVoid -> Log.d("FCM Token", "Token successfully saved to backend"))
                    .addOnFailureListener(e -> Log.e("FCM Token", "Error saving token", e));
        }
    }
}
