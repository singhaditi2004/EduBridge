package com.example.edubridge;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


public class Chats extends Fragment {

ImageButton searchBtt;
RecyclerView recyclerChatsFrag;

    public Chats() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        searchBtt= view.findViewById(R.id.searchImgButt);
        searchBtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(getActivity(), SearchUser.class));
            }
        });
        recyclerChatsFrag=view.findViewById(R.id.chatFragRecycle);
        setUpRecycleFragChat();
        return view;
    }

    private void setUpRecycleFragChat() {
    }
}