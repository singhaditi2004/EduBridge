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
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SearchUser extends AppCompatActivity {
    RecyclerView searchUser;
    ImageButton search,backButton;
    EditText enterName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_user);
        searchUser=findViewById(R.id.recycleUserSearch);
        search=findViewById(R.id.searchBtUser);
        enterName=findViewById(R.id.editTextSearchUser);
        backButton=findViewById(R.id.backButt);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchItem=enterName.getText().toString();
                if(searchItem.isEmpty() || searchItem.length()<3){
                     enterName.setError("Invalid Username");
                     return;
                }
                searchUserName(searchItem);
            }
        });
        enterName.requestFocus();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void searchUserName(String searchName){
        List<String> userN=new ArrayList<>();


    }
}