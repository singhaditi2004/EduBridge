package com.example.edubridge.Chat;

import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edubridge.Adapter.ChatRecyclerAdapter;
import com.example.edubridge.Adapter.SearchUserRecycleAdapter;
import com.example.edubridge.Model.ChatMessageModel;
import com.example.edubridge.Model.ChatRoomModel;
import com.example.edubridge.Model.UserModel;
import com.example.edubridge.R;
import com.example.edubridge.Utils.AndroidUtils;
import com.example.edubridge.Utils.FireBaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Callback;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {
    UserModel otherUser;
    EditText textMess;
    ChatRoomModel chatRoomModel;
    ChatRecyclerAdapter chatRecyclerAdapter;
    String chatRoomId;
    ImageButton backBut, sendBut;
    TextView userName;
    RecyclerView recycleChat;
    ImageView profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        otherUser = AndroidUtils.getUserModelFromIntent(getIntent());
        textMess = findViewById(R.id.editTetChat);
        chatRoomId = FireBaseUtil.getChatRoomId(FireBaseUtil.getCurrentUserId(), otherUser.getUserId());
        backBut = findViewById(R.id.backButt);
        sendBut = findViewById(R.id.imgButtSend);
        userName = findViewById(R.id.userNameChat);
        recycleChat = findViewById(R.id.chatsRecycle);
        profile = findViewById(R.id.ppChat);
        backBut.setOnClickListener(v -> {
            onBackPressed();
        });
        userName.setText(otherUser.getName());
        getOrCreateChatRoomModel();
        sendBut.setOnClickListener(v -> {
            String message = textMess.getText().toString().trim();
            if (message.isEmpty()) {
                return;
            }
            sendMessageToUser(message);
            setUpChatRecyclerView();
        });
        FireBaseUtil.getOtherUserProfilePic(otherUser.getUserId()).getDownloadUrl().addOnCompleteListener(t -> {
            if (t.isSuccessful()) {
                Uri uri = t.getResult();
                AndroidUtils.setProfilePic(this, uri, profile);

            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setUpChatRecyclerView() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Query the chats collection to find chat documents where the current user is a participant
        Query chatQuery = FireBaseUtil.getChatRoomMessageReference(chatRoomId).orderBy("timestamp", Query.Direction.DESCENDING);


        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>().setQuery(chatQuery, ChatMessageModel.class).build();

        // Set up the RecyclerView adapter
        chatRecyclerAdapter = new ChatRecyclerAdapter(options, getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recycleChat.setLayoutManager(manager);
        recycleChat.setAdapter(chatRecyclerAdapter);
        chatRecyclerAdapter.startListening();
        chatRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recycleChat.smoothScrollToPosition(0);
            }
        });
    }


    private void sendMessageToUser(String message) {
        chatRoomModel.setLastMessage(Timestamp.now());
        chatRoomModel.setLastMessSendId(FireBaseUtil.getCurrentUserId());
        chatRoomModel.setLastMessageM(message);
        FireBaseUtil.getChatRoomReference(chatRoomId).set(chatRoomModel);
        ChatMessageModel chatMessageModel = new ChatMessageModel(message, FireBaseUtil.getCurrentUserId(), Timestamp.now());
        FireBaseUtil.getChatRoomMessageReference(chatRoomId).add(chatMessageModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    textMess.setText("");
                    sendNotification(message);
                }
            }
        });
    }

    void sendNotification(String message) {
        FireBaseUtil.currentUserDetails().get().addOnCompleteListener(task->{
            if(task.isSuccessful()){
                UserModel  current=task.getResult().toObject(UserModel.class);

            }
        });

    }
    public void callAPI(JSONObject obj) {
        MediaType JSON = MediaType.get("application/json");
        OkHttpClient client = new OkHttpClient();

        // Replace with your Firebase Project ID
        String projectId = "edubridge-56977";
        String url = "https://fcm.googleapis.com/v1/projects/" + projectId + "/messages:send";

        // Generate or retrieve the access token (you need to implement this part)
        String accessToken = getAccessToken(); // This method should return the Bearer token

        // Prepare the request body
        RequestBody body = RequestBody.create(obj.toString(), JSON);

        // Prepare the request with the Authorization header
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer " + accessToken)  // Add Bearer token for authentication
                .build();

        // Execute the request in a separate thread (to avoid blocking the main thread)
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace(); // Handle failure
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Handle successful response
                    System.out.println("Notification sent successfully: " + response.body().string());
                } else {
                    // Handle error response
                    System.err.println("Error sending notification: " + response.body().string());
                }
            }
        });
    }

    public static String getAccessToken() {
        try {
            // Path to the service account JSON key file
            String jsonKeyPath = "C:/Users/Ashutosh Bais/AndroidStudioProjects/EduBridge/edubridge-56977-69efe0553909.json";
            // Load the credentials from the JSON file
            GoogleCredentials credentials = ServiceAccountCredentials.fromStream(new FileInputStream(jsonKeyPath))
                    .createScoped(Arrays.asList("https://www.googleapis.com/auth/firebase.messaging"));

            // Refresh the credentials (this gets the access token)
            credentials.refreshIfExpired();

            // Return the access token
            return credentials.getAccessToken().getTokenValue();
        } catch (IOException e) {
            e.printStackTrace();
            return null;  // Return null or handle the error as needed
        }
    }

    void getOrCreateChatRoomModel() {
        FireBaseUtil.getChatRoomReference(chatRoomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                chatRoomModel = task.getResult().toObject(ChatRoomModel.class);
                if (chatRoomModel == null) {
                    chatRoomModel = new ChatRoomModel(chatRoomId, Arrays.asList(FireBaseUtil.getCurrentUserId(), otherUser.getUserId()), Timestamp.now(), "");
                    FireBaseUtil.getChatRoomReference(chatRoomId).set(chatRoomModel);
                }
            }
        });

    }
}