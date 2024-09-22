package com.example.edubridge.Utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FireBaseUtil {

    public static DocumentReference currUser() {
        // Get the current user's UID from Firebase Authentication
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Reference to the Firestore database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create and return a DocumentReference to the user's document in the "users" collection
        return db.collection("users").document(uid);
    }
    public static boolean isLoogedIn(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return currentUser != null;
    }
    public static CollectionReference allChatsCollectionReference() {
        return FirebaseFirestore.getInstance().collection("chats");
    }
    public static CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("users");
    }
}
