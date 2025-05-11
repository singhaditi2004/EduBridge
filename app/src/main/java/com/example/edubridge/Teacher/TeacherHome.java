package com.example.edubridge.Teacher;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.LayoutAnimationController;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.edubridge.Adapter.CarouselAdapter;
import com.example.edubridge.Blogs;
import com.example.edubridge.Chat.Chats;
import com.example.edubridge.R;
import com.example.edubridge.Settings;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.os.Handler;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class TeacherHome extends AppCompatActivity {
    BottomNavigationView bottonNav;
    private ViewPager2 viewPager;
    private LinearLayout indicatorLayout;
    private List<Integer> images = Arrays.asList(R.drawable.aa, R.drawable.bb, R.drawable.cc,R.drawable.dd);
    private Handler slideHandler = new Handler();
    private int currentIndex = 0;
    private RecyclerView schoolRecycle;
    private ImageView maths, science, language, sports, art, history, music, special;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher_home);
        viewPager = findViewById(R.id.viewPager);
        indicatorLayout = findViewById(R.id.indicatorLayout);
        schoolRecycle = findViewById(R.id.schoolRecycle);
        maths = findViewById(R.id.maths);
        science = findViewById(R.id.science);
        language = findViewById(R.id.language);
        sports = findViewById(R.id.sports);
        art = findViewById(R.id.art);
        history = findViewById(R.id.history);
        music = findViewById(R.id.music);
        special = findViewById(R.id.special);
        setupAnimations();
        schoolRecycle.setItemAnimator(new DefaultItemAnimator());
        CarouselAdapter adapter = new CarouselAdapter(images);
        viewPager.setAdapter(adapter);

        setupIndicators(images.size());
        setCurrentIndicator(0);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                setCurrentIndicator(position);
                currentIndex = position;
            }
        });

        // Start auto slide
        slideHandler.postDelayed(slideRunnable, 3000);
        bottonNav = findViewById(R.id.bottomNav);
        bottonNav.setSelectedItemId(R.id.home);
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
    private Runnable slideRunnable = new Runnable() {
        @Override
        public void run() {
            currentIndex = (currentIndex + 1) % images.size();
            viewPager.setCurrentItem(currentIndex, true);
            slideHandler.postDelayed(this, 3000);
        }
    };

    private void setupIndicators(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(8, 0, 8, 0);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(this);
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    this, R.drawable.indicator_inactive));
            indicators[i].setLayoutParams(params);
            indicatorLayout.addView(indicators[i]);
        }
    }

    private void setCurrentIndicator(int index) {
        int childCount = indicatorLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) indicatorLayout.getChildAt(i);
            imageView.setImageDrawable(ContextCompat.getDrawable(
                    this, i == index ? R.drawable.indicator_active : R.drawable.indicator_inactive));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        slideHandler.removeCallbacks(slideRunnable); // Stop auto-sliding when activity is paused
    }

    @Override
    protected void onResume() {
        super.onResume();
        slideHandler.postDelayed(slideRunnable, 3000); // Resume auto-sliding
    }
    // Add this method to your activity
    private void setupAnimations() {
        // Set up ViewPager auto-scroll
        final int[] currentPage = {0};
        final Handler handler = new Handler();
        final Runnable update = new Runnable() {
            public void run() {
                if (currentPage[0] == viewPager.getAdapter().getItemCount()) {
                    currentPage[0] = 0;
                }
                viewPager.setCurrentItem(currentPage[0]++, true);
            }
        };

        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        }, 3000, 3000);

        // Add transition animations to RecyclerView items
        LayoutAnimationController animation = AnimationUtils
                .loadLayoutAnimation(this, R.anim.layout_animation_fall_down);
        schoolRecycle.setLayoutAnimation(animation);

        // Add click animation to category items
        setupClickAnimations(maths);
        setupClickAnimations(science);
        setupClickAnimations(language);
        setupClickAnimations(sports);
        setupClickAnimations(art);
        setupClickAnimations(history);
        setupClickAnimations(music);
        setupClickAnimations(special);
    }
    private void setupClickAnimations(View view) {
        view.setOnClickListener(v -> {
            // Scale animation
            v.animate()
                    .scaleX(0.9f)
                    .scaleY(0.9f)
                    .setDuration(100)
                    .withEndAction(() ->
                            v.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(100)
                                    .start()
                    )
                    .start();

            // Handle actual click functionality here
        });
    }

}
