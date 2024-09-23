package com.example.edubridge;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edubridge.Adapter.SearchUserRecycleAdapter;
import com.example.edubridge.Model.UserModel;
import com.example.edubridge.Utils.FireBaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class SearchUser extends AppCompatActivity {
    RecyclerView searchUser;
    ImageButton search, backButton;
    EditText name;
    SearchUserRecycleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_user);
        searchUser = findViewById(R.id.recycleUserSearch);
        search = findViewById(R.id.searchBtUser);
        name = findViewById(R.id.editTextSearchUser);
        backButton = findViewById(R.id.backButt);
        name.requestFocus();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchItem = name.getText().toString();
                if (searchItem.isEmpty() || searchItem.length() < 3) {
                    name.setError("Invalid Username");
                    return;
                }
                setupSearchRecyclerView(searchItem);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    void setupSearchRecyclerView(String searchItem) {
        // Get the current user ID
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Query the chats collection to find chat documents where the current user is a participant
        Query chatQuery = FireBaseUtil.allChatsCollectionReference()
                .whereArrayContains("participants", currentUserId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        chatQuery.get().addOnSuccessListener(querySnapshot -> {
            List<String> contactIds = new ArrayList<>();

            // Loop through chat documents to extract participant IDs (excluding the current user)
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                List<String> participants = (List<String>) document.get("participants");
                for (String participantId : participants) {
                    if (!participantId.equals(currentUserId)) {
                        contactIds.add(participantId);
                    }
                }
            }

            // Query the users collection for the contacts matching the searchItem
            Query userQuery = FireBaseUtil.allUserCollectionReference()
                    .whereIn("userId", contactIds)
                    .whereGreaterThanOrEqualTo("name", searchItem)
                    .orderBy("name", Query.Direction.ASCENDING);

            FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                    .setQuery(userQuery, UserModel.class)
                    .build();

            // Set up the RecyclerView adapter
            adapter = new SearchUserRecycleAdapter(options, getApplicationContext());
            searchUser.setLayoutManager(new LinearLayoutManager(this));
            searchUser.setAdapter(adapter);
            adapter.startListening();
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.startListening();
        }
    }
}