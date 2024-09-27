package com.example.edubridge.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edubridge.R;
import com.example.edubridge.Model.ChatMessageModel;
import com.example.edubridge.Utils.FireBaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessageModel, ChatRecyclerAdapter.ChatMessageModelViewHolder> {

    Context context;

    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatMessageModelViewHolder holder, int position, @NonNull ChatMessageModel model) {
        if(model.getSenderld()== FireBaseUtil.getCurrentUserId()){
            holder.left.setVisibility(View.GONE);
            holder.right.setVisibility(View.VISIBLE);
            holder.sender.setText(model.getMessage());
        }
        else {
            holder.right.setVisibility(View.GONE);
            holder.left.setVisibility(View.VISIBLE);
            holder.reciever.setText(model.getMessage());
        }
    }

    @NonNull
    @Override
    public ChatMessageModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_recycler_row, parent, false);
        return new ChatMessageModelViewHolder(view);
    }

    class ChatMessageModelViewHolder extends RecyclerView.ViewHolder {
      TextView sender,reciever;
        LinearLayout right,left;
        public ChatMessageModelViewHolder(@NonNull View itemView) {
            super(itemView);
             right=itemView.findViewById(R.id.rightChatLay);
             left=itemView.findViewById(R.id.leftChatLay);
            sender = itemView.findViewById(R.id.leftChatText);
            reciever = itemView.findViewById(R.id.rightChatText);
      

        }
    }
}
