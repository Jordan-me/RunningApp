package com.example.runningapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class SaveRunActivity extends AppCompatActivity {

    public static String RUN_DISTANCE = "RUN_DISTANCE";
    public static String RUN_TIME = "RUN_TIME";
    public static String RUN_ROUTE = "RUN_ROUTE";

    public String run_txt_distance ;
    public String run_txt_time;
    public List<LatLng> run_arr_route;
    private MaterialButton save_BTN_save;
    private TextInputEditText save_TXT_title;
    private TextInputEditText save_TXT_info;
    private AppCompatSpinner save_SPINNER_visibility;
    private MaterialButton save_BTN_discard;
    private FirebaseDatabaseManager firebaseDatabaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_run);
        Bundle extras = getIntent().getBundleExtra("Bundle");

        hideSystemUI(this);
        findViews();
        initViews();
    }

    private void initViews() {
        Bundle extras = getIntent().getBundleExtra("Bundle");
        run_txt_distance = extras.getString(RUN_DISTANCE);
        run_txt_time = extras.getString(RUN_TIME);
        run_arr_route = new Gson().fromJson(getIntent().getStringExtra(RUN_ROUTE), new TypeToken<List<LatLng>>(){}.getType());
        save_BTN_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    saveRunActivity();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        save_BTN_discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                discardRunActivity();
            }
        });
    }
    private void saveRunActivity() throws ParseException {
        if(TextUtils.isEmpty(save_TXT_title.getText().toString())){
            //show an error message
            save_TXT_title.setError("Title is required");
            return;
        }
        if(TextUtils.isEmpty(save_TXT_info.getText().toString())){
            save_TXT_info.setText("NA");
        }
        SimpleDateFormat format = new SimpleDateFormat("EEE, MMM d, yyyy");
        Date date = Calendar.getInstance().getTime();
        Run runToSave = new Run()
                .setRunId(UUID.randomUUID().toString())
                .setUserId(MSPV.getInstance().getString(MSPV.USERID,""))
                .setDate(format.format(date))
                .setDistance(run_txt_distance)
                .setMoreInfo(save_TXT_info.getText().toString())
                .setTime(run_txt_time)
                .setTitle(save_TXT_title.getText().toString())
                .setVisibleEveryone(save_SPINNER_visibility.getSelectedItemPosition() == 0)
                .setRoute(run_arr_route);
        firebaseDatabaseManager.addRunToDatabase(runToSave);
        Toast.makeText(this, "Your activity has been save successfully!", Toast.LENGTH_SHORT).show();
        finishActivity();
   }

    private void finishActivity() {
        Intent intent = new Intent(this, NavigationActivity.class);
        startActivity(intent);
        finish();
    }

    private void discardRunActivity() {
        finishActivity();

    }

    private void findViews() {
        firebaseDatabaseManager = FirebaseDatabaseManager.getInstance();
        save_BTN_save = findViewById(R.id.save_BTN_save);
        save_TXT_title = findViewById(R.id.save_TXT_title);
        save_TXT_info = findViewById(R.id.save_TXT_info);
        save_SPINNER_visibility = findViewById(R.id.save_SPINNER_visibility);
        save_BTN_discard = findViewById(R.id.save_BTN_discard);
    }


    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI(this);
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

}