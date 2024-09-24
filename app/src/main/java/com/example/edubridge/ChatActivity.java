package com.example.edubridge;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edubridge.Adapter.ChatRecyclerAdapter;
import com.example.edubridge.Model.ChatMessageModel;
import com.example.edubridge.Model.ChatRoomModel;
import com.example.edubridge.Model.UserModel;
import com.example.edubridge.Utils.AndroidUtils;
import com.example.edubridge.Utils.FireBaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {
    UserModel otherUser;
    EditText textMess;
    ChatRoomModel chatRoomModel;
    ChatRecyclerAdapter chatRecyclerAdapter;
    String chatRoomId;
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
        chatRoomId= FireBaseUtil.getChatRoomId(FireBaseUtil.getCurrentUserId(),otherUser.getUserId());
        backBut=findViewById(R.id.backButt);
        sendBut=findViewById(R.id.imgButtSend);
        userName=findViewById(R.id.userNameChat);
        recycleChat=findViewById(R.id.chatsRecycle);
        backBut.setOnClickListener(v -> {
            onBackPressed();
        });
        userName.setText(otherUser.getName());
        getOrCreateChatRoomModel();
        sendBut.setOnClickListener(v -> {
            String message=textMess.getText().toString().trim();
            if(message.isEmpty()){
                return;
            }
            sendMessageToUser(message);
            setUpChatRecyclerView();
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setUpChatRecyclerView() {

    }

    private void sendMessageToUser(String message) {
        chatRoomModel.setLastMessage(Timestamp.now());
        chatRoomModel.setLastMessSendId(FireBaseUtil.getCurrentUserId());
        chatRoomModel.setLastMessageM(message);
        FireBaseUtil.getChatRoomReference(chatRoomId).set(chatRoomModel);
        ChatMessageModel chatMessageModel=new ChatMessageModel(message,FireBaseUtil.getCurrentUserId(),Timestamp.now());
        FireBaseUtil.getChatRoomMessageReference(chatRoomId).add(chatMessageModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    textMess.setText("");
                }
            }
        });
    }

    void getOrCreateChatRoomModel(){
        FireBaseUtil.getChatRoomReference(chatRoomId).get().addOnCompleteListener(task->{
            if(task.isSuccessful()){
                chatRoomModel=task.getResult().toObject(ChatRoomModel.class);
                if(chatRoomModel==null){
                    chatRoomModel=new ChatRoomModel(chatRoomId, Arrays.asList(FireBaseUtil.getCurrentUserId(),otherUser.getUserId()), Timestamp.now(),"");
                    FireBaseUtil.getChatRoomReference(chatRoomId).set(chatRoomModel);
                }
            }
        });

    }
}