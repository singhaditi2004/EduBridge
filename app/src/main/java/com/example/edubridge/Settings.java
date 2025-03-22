package com.example.edubridge;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.edubridge.Utils.FireBaseUtil;
import com.google.firebase.messaging.FirebaseMessaging;

public class Settings extends Fragment {
    LinearLayout logout;

    public Settings() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        logout = view.findViewById(R.id.logOut);

        // Set up the logout button click listener
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete the Firebase Messaging token
                FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Log out the user
                        FireBaseUtil.logOut();

                        // Redirect to the SplashScreen activity
                        Intent i = new Intent(getContext(), SplashScreen.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);

                        // Log the logout action
                        Log.d("Settings", "User logged out successfully");
                    } else {
                        // Log the error if token deletion fails
                        Log.e("Settings", "Failed to delete FCM token", task.getException());
                    }
                });
            }
        });

        return view;
    }
}