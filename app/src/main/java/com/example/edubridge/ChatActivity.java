package com.example.edubridge;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edubridge.Utils.AndroidUtils;

public class ChatActivity extends AppCompatActivity {
    UserModel otherUser;
    EditText textMess;
    ImageButton backBut,sendBut;
    TextView userName;
    RecyclerView recycleChat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        otherUser= AndroidUtils.getUserModelFromIntent(getIntent());
        textMess=findViewById(R.id.editTetChat);
        backBut=findViewById(R.id.backButt);
        sendBut=findViewById(R.id.imgButtSend);
        userName=findViewById(R.id.userNameChat);
        recycleChat=findViewById(R.id.chatsRecycle);
        backBut.setOnClickListener(v -> {
            onBackPressed();
        });
        userName.setText(otherUser.getName());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}