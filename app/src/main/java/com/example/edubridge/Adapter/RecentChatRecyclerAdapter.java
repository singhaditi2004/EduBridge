package com.example.edubridge.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edubridge.Chat.ChatActivity;
import com.example.edubridge.Model.UserModel;
import com.example.edubridge.R;
import com.example.edubridge.Model.ChatRoomModel;
import com.example.edubridge.Utils.AndroidUtils;
import com.example.edubridge.Utils.FireBaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatRoomModel, RecentChatRecyclerAdapter.ChatRoomModelViewHolder> {

    Context context;

    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatRoomModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatRoomModelViewHolder holder, int position, @NonNull ChatRoomModel model) {
        FireBaseUtil.getOtherUserFromChatRoom(model.getUserid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                boolean lastMessageSentByMe = model.getLastMessSendId().equals(FireBaseUtil.getCurrentUserId());

                UserModel otherUserModel = task.getResult().toObject(UserModel.class);
                FireBaseUtil.getOtherUserProfilePic(otherUserModel.getUserId()).getDownloadUrl().addOnCompleteListener(t -> {
                    if (t.isSuccessful()) {
                        Uri uri = t.getResult();
                        AndroidUtils.setProfilePic(context, uri, holder.profilePic);

                    }
                });
                holder.userName.setText(otherUserModel.getName());
                if (lastMessageSentByMe) {
                    holder.lastMessage.setText("You : " + (model.getLastMessageM()));
                } else {
                    holder.lastMessage.setText(model.getLastMessageM());
                }
                holder.lastMessageTime.setText(FireBaseUtil.timeStampToString(model.getLastMessage()));
                holder.itemView.setOnClickListener(v -> {
                    Intent i = new Intent(context, ChatActivity.class);
                    AndroidUtils.passModelAsIntent(i, otherUserModel);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);

                });
            }
        });
    }

    @NonNull
    @Override
    public ChatRoomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row, parent, false);
        return new ChatRoomModelViewHolder(view);
    }

    class ChatRoomModelViewHolder extends RecyclerView.ViewHolder {
        TextView lastMessage, userName, lastMessageTime;
        ImageView profilePic;

        public ChatRoomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            lastMessage = itemView.findViewById(R.id.messageFragChat);
            userName = itemView.findViewById(R.id.userNameFragChat);
            lastMessageTime = itemView.findViewById(R.id.timeFragChat);

        }
    }
}
