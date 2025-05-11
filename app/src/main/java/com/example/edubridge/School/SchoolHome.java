package com.example.edubridge.School;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.edubridge.Adapter.CarouselAdapter;
import com.example.edubridge.Blogs;
import com.example.edubridge.Chat.Chats;
import com.example.edubridge.R;
import com.example.edubridge.Settings;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;
import java.util.List;

public class SchoolHome extends AppCompatActivity {
    BottomNavigationView bottomNav;
    private ViewPager2 viewPager;
    private LinearLayout indicatorLayout;
    private List<Integer> images = Arrays.asList(R.drawable.aa, R.drawable.bb, R.drawable.cc, R.drawable.dd);
    private Handler slideHandler = new Handler();
    private int currentIndex = 0;
    private RecyclerView teachersRecyclerView;
    private LinearLayout categoryHome, categoryTeachers, categorySubjects, categoryEvents,
            categoryResults, categoryTimetable, categoryNotices, categorySettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_school_home);

        initializeViews();
        setupRecyclerView();
        setupCarousel();
        setupBottomNavigation();
        setupAllAnimations();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void initializeViews() {
        viewPager = findViewById(R.id.viewPager2);
        indicatorLayout = findViewById(R.id.indicatorLayout);
        teachersRecyclerView = findViewById(R.id.teachersRecyclerView);

        categoryHome = findViewById(R.id.categoryHome);
        categoryTeachers = findViewById(R.id.categoryTeachers);
        categorySubjects = findViewById(R.id.categorySubjects);
        categoryEvents = findViewById(R.id.categoryEvents);
        categoryResults = findViewById(R.id.categoryResults);
        categoryTimetable = findViewById(R.id.categoryTimetable);
        categoryNotices = findViewById(R.id.categoryNotices);
        categorySettings = findViewById(R.id.categorySettings);
    }
    private void setupRecyclerView() {
        teachersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        teachersRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void setupCarousel() {
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

        slideHandler.postDelayed(slideRunnable, 3000);
    }

    private void setupBottomNavigation() {
        bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.home);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            resetAllNavIcons();
            scaleSelectedNavIcon(id);

            if (R.id.home == id) {
                restartActivity();
                return true;
            } else if (R.id.chat == id) {
                loadFrag(new Chats());
                return true;
            } else if (R.id.settings == id) {
                loadFrag(new Settings());
                return true;
            } else if (R.id.jobs == id) {
                loadFrag(new school_jobs());
                return true;
            } else {
                loadFrag(new Blogs());
                return true;
            }
        });
    }
    private void resetAllNavIcons() {
        for (int i = 0; i < bottomNav.getMenu().size(); i++) {
            MenuItem menuItem = bottomNav.getMenu().getItem(i);
            View iconView = bottomNav.findViewById(menuItem.getItemId())
                    .findViewById(com.google.android.material.R.id.icon);
            if (iconView != null) scaleIcon(iconView, true);
        }
    }

    private void scaleSelectedNavIcon(int id) {
        View selectedIconView = bottomNav.findViewById(id)
                .findViewById(com.google.android.material.R.id.icon);
        if (selectedIconView != null) scaleIcon(selectedIconView, false);
    }

    private void restartActivity() {
        Intent intent = new Intent(getApplicationContext(), SchoolHome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void setupAllAnimations() {
        // RecyclerView animation
        LayoutAnimationController animation = AnimationUtils
                .loadLayoutAnimation(this, R.anim.layout_animation_fall_down);
        teachersRecyclerView.setLayoutAnimation(animation);

        // Category button animations with press effects
        setupCategoryButtonAnimations(categoryHome);
        setupCategoryButtonAnimations(categoryTeachers);
        setupCategoryButtonAnimations(categorySubjects);
        setupCategoryButtonAnimations(categoryEvents);
        setupCategoryButtonAnimations(categoryResults);
        setupCategoryButtonAnimations(categoryTimetable);
        setupCategoryButtonAnimations(categoryNotices);
        setupCategoryButtonAnimations(categorySettings);

        // Banner zoom animation
        setupBannerAnimation();
    }

    private void setupCategoryButtonAnimations(LinearLayout categoryButton) {
        final Animation scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_animation);
        final Animation scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_return_animation);

        categoryButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.startAnimation(scaleDown);
            } else if (event.getAction() == MotionEvent.ACTION_UP ||
                    event.getAction() == MotionEvent.ACTION_CANCEL) {
                v.startAnimation(scaleUp);
              //  Toast.makeText(this, "Category Clicked", Toast.LENGTH_SHORT).show();
                handleCategoryClick(v.getId());
            }
            return false;
        });

        // Additional click animation
        categoryButton.setOnClickListener(v -> {
            v.animate()
                    .scaleX(0.9f).scaleY(0.9f).setDuration(100)
                    .withEndAction(() -> v.animate()
                            .scaleX(1f).scaleY(1f).setDuration(100).start())
                    .start();
        });
    }

    private void handleCategoryClick(int viewId) {
        /*switch (viewId) {
            case R.id.categoryTeachers:
                Toast.makeText(this, "Teachers Management", Toast.LENGTH_SHORT).show();
                // Implement teachers fragment loading
                break;
            case R.id.categorySubjects:
                Toast.makeText(this, "Subjects Management", Toast.LENGTH_SHORT).show();
                // Implement subjects fragment loading
                break;
            // Add cases for other categories
            default:
                Toast.makeText(this, "Category Selected", Toast.LENGTH_SHORT).show();
        }*/
        Toast.makeText(this, "Category clicked", Toast.LENGTH_SHORT).show();
    }

    private void setupBannerAnimation() {
        Animation zoomAnimation = new ScaleAnimation(
                1.0f, 1.05f, 1.0f, 1.05f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        zoomAnimation.setDuration(5000);
        zoomAnimation.setRepeatCount(Animation.INFINITE);
        zoomAnimation.setRepeatMode(Animation.REVERSE);
        //bannerImage.startAnimation(zoomAnimation);
    }
    public void loadFrag(Fragment frag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction tx = fm.beginTransaction();

        ScrollView scrollView = findViewById(R.id.scrollView2);
        scrollView.setVisibility(View.GONE);

        FrameLayout containerFrag = findViewById(R.id.containerFrag);
        containerFrag.setVisibility(View.VISIBLE);

        tx.replace(R.id.containerFrag, frag);
        tx.addToBackStack(null);
        tx.commit();
    }

    private void scaleIcon(View iconView, boolean isSelected) {
        float scaleValue = isSelected ? 1.5f : 1f;
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

    private void setupAnimations() {
        // RecyclerView animation
        LayoutAnimationController animation = AnimationUtils
                .loadLayoutAnimation(this, R.anim.layout_animation_fall_down);
        teachersRecyclerView.setLayoutAnimation(animation);

        // Category button animations
        setupClickAnimations(categoryHome);
        setupClickAnimations(categoryTeachers);
        setupClickAnimations(categorySubjects);
        setupClickAnimations(categoryEvents);
        setupClickAnimations(categoryResults);
        setupClickAnimations(categoryTimetable);
        setupClickAnimations(categoryNotices);
        setupClickAnimations(categorySettings);
    }

    private void setupClickAnimations(View view) {
        view.setOnClickListener(v -> {
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

            // Handle category-specific actions here
           /* switch (v.getId()) {
                case R.id.categoryTeachers:
                    // Open teachers list
                    break;
                case R.id.categorySubjects:
                    // Open subjects list
                    break;
                // Add other cases as needed
            }*/
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        slideHandler.removeCallbacks(slideRunnable);
        // Clear animations to prevent memory leaks
    }

    @Override
    protected void onResume() {
        super.onResume();
        slideHandler.postDelayed(slideRunnable, 3000);
        // Restart banner animation if needed

    }
}