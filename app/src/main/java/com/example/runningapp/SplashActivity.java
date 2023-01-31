package com.example.runningapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {
    final int ANIM_DURATION = 4400;
    private ImageView splash_IMG_background;
    private ShapeableImageView splash_IMG_logo;
    private boolean isActivityNext = false;
    private int counter = 0;
    private TimerTask ts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        hideSystemUI(this);
        findViews();
        initViews();
        showViewAnimation(splash_IMG_logo);


    }
    private void showViewAnimation(View v) {
        v.setVisibility(View.VISIBLE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        v.setY(-height / 2);
        v.setScaleY(0.0f);
        v.setScaleX(0.0f);
        v.animate()
                .scaleY(1.0f)
                .scaleX(1.0f)
                .translationY(0)
                .setDuration(ANIM_DURATION)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Timer timer = new Timer();
                        timer.scheduleAtFixedRate(ts = new TimerTask() {
                            @Override
                            public void run() {
                                runOnUiThread(() -> {
                                    counter++;
                                    if(counter ==10)
                                        startNextActivity();
                                });
                            }
                        }, 0, 200);
                    }
                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }
                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
    }

    private void startNextActivity() {
        if(!isActivityNext) {
            isActivityNext = true;
            Intent intent;
            if(MSPV.getInstance().getBoolean("isLoggedIn", false)) {
                // The user is already logged in, redirect them to the main activity
                Bundle bundle = new Bundle();
                bundle.putString(MSPV.USERID,MSPV.getInstance().getString(MSPV.USERID,""));
//                intent = new Intent(this, RunActivity.class);
                intent = new Intent(this, NavigationActivity.class);
                intent.putExtra("Bundle", bundle);
            } else {
                // The user is not logged in, redirect them to the login activity
                intent = new Intent(this, LoginActivity.class);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();
        }
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

    private void initViews() {
        splash_IMG_logo.setVisibility(View.INVISIBLE);
        splash_IMG_background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNextActivity();
            }
        });
    }
    private void findViews() {
        splash_IMG_background = findViewById(R.id.splash_IMG_background);
        splash_IMG_logo = findViewById(R.id.splash_IMG_logo);
    }
    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI(this);
    }
}