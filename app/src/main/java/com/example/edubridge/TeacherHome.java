package com.example.edubridge;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;

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

                // Reset scale for all icons to default size
                for (int i = 0; i < bottonNav.getMenu().size(); i++) {
                    MenuItem menuItem = bottonNav.getMenu().getItem(i);
                    View iconView = bottonNav.findViewById(menuItem.getItemId()).findViewById(com.google.android.material.R.id.icon);
                    if (iconView != null) {
                        scaleIcon(iconView, true);
                    }
                }

                // Get the selected item's icon view and scale it up
                View selectedIconView = bottonNav.findViewById(id).findViewById(com.google.android.material.R.id.icon);
                if (selectedIconView != null) {
                    scaleIcon(selectedIconView, false);
                }

                if (R.id.home == id) {
                    Intent intent = new Intent(getApplicationContext(), TeacherHome.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (R.id.chat == id) {
                    loadFrag(new Chats());
                    return true;
                } else if (R.id.settings == id) {
                    loadFrag(new Settings());
                    return true;
                } else if (R.id.jobs == id) {
                    loadFrag(new Jobs());
                    return true;
                } else {
                    loadFrag(new Blogs());
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
    public void loadFrag(Fragment frag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction tx = fm.beginTransaction();

        // Hide the ScrollView when a fragment is loaded
        ScrollView scrollView = findViewById(R.id.scrollView2);
        scrollView.setVisibility(View.GONE);

        // Make the fragment container visible
        FrameLayout containerFrag = findViewById(R.id.containerFrag);
        containerFrag.setVisibility(View.VISIBLE);

        // Load the fragment into the container
        tx.replace(R.id.containerFrag, frag);
        tx.addToBackStack(null);
        tx.commit();
    }
    private void scaleIcon(View iconView, boolean isSelected) {
        float scaleValue = isSelected ? 1.5f : 1f; // 1.5x scale for selected, 1x for deselected
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(iconView, "scaleX", scaleValue);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(iconView, "scaleY", scaleValue);
        scaleX.setDuration(150);
        scaleY.setDuration(150);
        AnimatorSet scaleAnimation = new AnimatorSet();
        scaleAnimation.playTogether(scaleX, scaleY);
        scaleAnimation.start();
    }
}
