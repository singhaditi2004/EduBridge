package com.example.edubridge.Chat;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.edubridge.Adapter.RecentChatRecyclerAdapter;
import com.example.edubridge.Adapter.SearchUserRecycleAdapter;
import com.example.edubridge.Model.ChatRoomModel;
import com.example.edubridge.Model.UserModel;
import com.example.edubridge.R;
import com.example.edubridge.Utils.FireBaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;


public class Chats extends Fragment {

    ImageButton searchBtt;
    RecyclerView recyclerChatsFrag;
    RecentChatRecyclerAdapter recentChatAdapter;

    public Chats() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        searchBtt = view.findViewById(R.id.searchImgButt);
        searchBtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SearchUser.class));
            }
        });
        recyclerChatsFrag = view.findViewById(R.id.chatFragRecycle);
        setUpRecycleFragChat();
        return view;
    }

    void setUpRecycleFragChat() {
        // Get the current user ID
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Query the chats collection to find chat documents where the current user is a participant
        Query chatQuery = FireBaseUtil.allChatRoomCollectionReference().whereArrayContains("userIds", FireBaseUtil.getCurrentUserId())
                .orderBy("lastMessageTimeStamp", Query.Direction.DESCENDING);


        FirestoreRecyclerOptions<ChatRoomModel> options = new FirestoreRecyclerOptions.Builder<ChatRoomModel>().setQuery(chatQuery, ChatRoomModel.class).build();

        // Set up the RecyclerView adapter
        recentChatAdapter = new RecentChatRecyclerAdapter(options, getContext());
        recyclerChatsFrag.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerChatsFrag.setAdapter(recentChatAdapter);
        recentChatAdapter.startListening();

    }


    @Override
    public void onStart() {
        super.onStart();
        if (recentChatAdapter != null) {
            recentChatAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (recentChatAdapter != null) {
            recentChatAdapter.stopListening();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (recentChatAdapter != null) {
            recentChatAdapter.startListening();
        }
    }
}