package com.example.edubridge.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edubridge.R;
import com.example.edubridge.UserModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class SearchUserRecycleAdapter extends FirestoreRecyclerAdapter<UserModel, SearchUserRecycleAdapter.UserModetViewHolder> {

    Context context;

    public SearchUserRecycleAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserModetViewHolder holder, int position, @NonNull UserModel model) {
        holder.userName.setText(model.getName());
        holder.phone.setText(model.getPhone());
        holder.itemView.setOnClickListener(v -> {

        });
    }

    @NonNull
    @Override
    public UserModetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_user_recycle_row, parent, false);
        return new UserModetViewHolder(view);
    }

    class UserModetViewHolder extends RecyclerView.ViewHolder {
        TextView phone, userName;
        ImageView profilePic;

        public UserModetViewHolder(@NonNull View itemView) {
            super(itemView);
            phone = itemView.findViewById(R.id.phoner);
            userName = itemView.findViewById(R.id.userName);
            profilePic = itemView.findViewById(R.id.profilepicview);

        }
    }
}
