package com.example.edubridge;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class TeacherHome extends AppCompatActivity {
    BottomNavigationView bottonNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher_home);
        ViewPager2 viewPager2 = findViewById(R.id.viewPager);
       // TabLayout tabLayout = findViewById(R.id.tabLayout);
      //  new TabLayoutMediator(tabLayout, viewPager2,
         //       (tab, position) -> {
        //        }).attach();
        bottonNav = findViewById(R.id.bottomNav);
        bottonNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (R.id.home == id) {
                    Intent intent = new Intent(getApplicationContext(), TeacherHome.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                else if (R.id.chat == id) {
                    loadFrag(new Chats());
                    return true;
                }
                else if (R.id.settings == id) {
                    return true;
                }
                else if (R.id.jobs == id) {
                    return true;
                }
                else {
                    return true;
                }
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public  void loadFrag(Fragment frag){
        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction tx=fm.beginTransaction();
        tx.add(R.id.containerFrag, frag);
        tx.commit();
    }
}