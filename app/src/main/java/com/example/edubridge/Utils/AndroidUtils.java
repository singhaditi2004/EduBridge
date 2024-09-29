package com.example.edubridge.Utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.edubridge.Model.UserModel;

public class AndroidUtils {
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void passModelAsIntent(Intent intent, UserModel model) {
        intent.putExtra("userName", model.getName());
        intent.putExtra("phone", model.getPhone());
        intent.putExtra("userId", model.getUserId());


    }
    public static UserModel getUserModelFromIntent(Intent intent){
        UserModel userModel=new UserModel();
        userModel.setName(intent.getStringExtra("userName"));
        userModel.setPhone(intent.getStringExtra("phone"));
        userModel.setUserId(intent.getStringExtra("userId"));
        return userModel;
    }
    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView){
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }
}
