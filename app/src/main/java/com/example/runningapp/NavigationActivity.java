package com.example.runningapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class NavigationActivity extends AppCompatActivity {
    private BottomNavigationView navigation_BAR_navbar;
    private FrameLayout navigation_FRAME_container;
    private Fragment selectedFragment;
    private Toolbar navigation_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        hideSystemUI(this);
        findViews();
        initViews();
    }

    private void initViews() {
        // By default, set the home page as the selected fragment
        navigation_BAR_navbar.setSelectedItemId(R.id.nav_home);
        navigation_BAR_navbar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        // Handle the home page selection
                        navigateHomeActivity();
                        break;
                    case R.id.nav_run:
                        // Handle the run page selection
                        navigateRunActivity();
                        break;
                    case R.id.nav_profile:
                        // Handle the profile page selection
                        navigateProfileActivity();
                        break;
                }
                return true;

            }
        });

        navigation_BAR_navbar.setOnItemReselectedListener(new NavigationBarView.OnItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        // Handle the home page selection
                        navigateHomeActivity();
                        break;
                    case R.id.nav_run:
                        // Handle the run page selection
                        navigateRunActivity();
                        break;
                    case R.id.nav_profile:
                        // Handle the profile page selection
                        navigateProfileActivity();
                        break;
                }
            }
        });
        navigateHomeActivity();
    }

    private void navigateProfileActivity() {
        getSupportActionBar().setTitle("You");
        selectedFragment = new ProfileFragment();
        ((ProfileFragment)selectedFragment).setActivity(this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.navigation_FRAME_container, selectedFragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    private void navigateHomeActivity() {
        getSupportActionBar().setTitle("Home");
        selectedFragment  = new HomeFragment();
        ((HomeFragment)selectedFragment).setActivity(this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.navigation_FRAME_container, selectedFragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    private void navigateRunActivity() {
        Intent intent = new Intent(this, RunActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    private void findViews() {
        navigation_toolbar = findViewById(R.id.navigation_toolbar);
        setSupportActionBar(navigation_toolbar);
        navigation_BAR_navbar = findViewById(R.id.navigation_BAR_navbar);
        navigation_FRAME_container = findViewById(R.id.navigation_FRAME_container);
    }

    private void hideSystemUI(Activity activity) {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE
        View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        // Dim the Status and Navigation Bars
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE);

        // Without - cut out display
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            activity.getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI(this);
    }
}