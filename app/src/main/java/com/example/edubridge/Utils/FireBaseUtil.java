package com.example.edubridge.Utils;

import android.util.Log;

import com.example.edubridge.Teacher.TeacherProfile;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.List;

public class FireBaseUtil {

    public static DocumentReference currUser() {
        // Get the current user's UID from Firebase Authentication
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Reference to the Firestore database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create and return a DocumentReference to the user's document in the "users" collection
        return db.collection("users").document(uid);
    }
    public static String getCurrentUserId(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return uid;
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
    public static DocumentReference getChatRoomReference(String chatRoomId){
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatRoomId);
    }
    public static String getChatRoomId(String usId1,String usId2){
        if(usId1.hashCode()<usId2.hashCode()){
            return usId1+"_"+usId2;
        }
        else{
            return usId2+"_"+usId1;
        }
    }
    public static CollectionReference getChatRoomMessageReference(String chatRoomId){
        return getChatRoomReference(chatRoomId).collection("chats");
    }
    public static CollectionReference allChatRoomCollectionReference(){
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }
    public static DocumentReference getOtherUserFromChatRoom(List<String>list){
        if(list.get(0).equals(FireBaseUtil.getCurrentUserId())){
          return  allUserCollectionReference().document(list.get(1));
        }
        else{
            return  allUserCollectionReference().document(list.get(0));
        }
    }
    public static String timeStampToString(Timestamp timeStamp){
       return new SimpleDateFormat("HH:MM").format(timeStamp.toString());
    }
    public static void logOut(){
        FirebaseAuth.getInstance().signOut();
    }
    public static StorageReference getCurrentUserProfilePic(){
        return FirebaseStorage.getInstance().getReference().child("profile_images").child(TeacherProfile.getEmailIdAuth());
    }
    public static StorageReference getOtherUserProfilePic(String userId){
        return FirebaseStorage.getInstance().getReference().child("profile_images").child(userId);
    }
    public static DocumentReference currentUserDetails(){
        return FirebaseFirestore.getInstance().collection("users").document(FireBaseUtil.getCurrentUserId());
    }
    // Add a method to fetch any user's document based on their userId
    public static DocumentReference currentUserDetails(String userId) {
        return FirebaseFirestore.getInstance().collection("users").document(userId);
    }


}
