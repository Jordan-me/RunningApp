package com.example.runningapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    public static final String IS_FIRST_TIME = "IS_FIRST_TIME";
    public boolean isFirstTime = false;
    public boolean updated = false;
    private Toolbar edit_toolbar;
    private MaterialButton edit_BTN_done;
    private CircleImageView edit_profile_image;
    private TextInputEditText edit_TXT_first_name;
    private TextInputEditText edit_TXT_last_name;
    private TextInputEditText edit_TXT_city;
    private TextInputEditText edit_TXT_state;
    private TextInputEditText edit_TXT_bio;
    private AppCompatSpinner edit_SPINNER_gender;
    private TextInputEditText edit_TXT_weight;
    private TextInputEditText edit_TXT_birthdate;

    private String urlImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        updated = false;
        Bundle extras = getIntent().getBundleExtra("Bundle");
        if (extras != null) {
            isFirstTime = extras.getBoolean(IS_FIRST_TIME);
        }
        hideSystemUI(this);
        findViews();
        initViews();
    }

    private void findViews() {
        edit_toolbar = findViewById(R.id.edit_toolbar);
        edit_BTN_done = findViewById(R.id.edit_BTN_done);
        edit_profile_image = findViewById(R.id.edit_profile_image);
        edit_TXT_first_name = findViewById(R.id.edit_TXT_first_name);
        edit_TXT_last_name = findViewById(R.id.edit_TXT_last_name);
        edit_TXT_city = findViewById(R.id.edit_TXT_city);
        edit_TXT_state = findViewById(R.id.edit_TXT_state);
        edit_TXT_bio = findViewById(R.id.edit_TXT_bio);
        edit_SPINNER_gender = findViewById(R.id.edit_SPINNER_gender);
        edit_TXT_weight = findViewById(R.id.edit_TXT_weight);
        edit_TXT_birthdate = findViewById(R.id.edit_TXT_birthdate);
        urlImage = MSPV.getInstance().getString(MSPV.USER_IMAGE,"");
        setSupportActionBar(edit_toolbar);
        if(!isFirstTime) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        }
    }

    private void initViews() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Profile");
        Glide.with(EditProfileActivity.this)
                .load( MSPV.getInstance().getString(MSPV.USER_IMAGE,""))
                .error(R.drawable.ic_profile)
                .into(edit_profile_image);
        String name =  MSPV.getInstance().getString(MSPV.USER_NAME,"");
        if (!name.isEmpty()){
            edit_TXT_first_name.setText(name.split(" ")[0]);
            edit_TXT_last_name.setText(name.split(" ")[1]);
        }

        edit_toolbar.setNavigationOnClickListener(v -> finishCurrentActivity());
        edit_BTN_done.setOnClickListener(view -> updateUser());
        edit_TXT_birthdate.setInputType(InputType.TYPE_NULL);
        edit_profile_image.setOnClickListener(view -> pickUpPhoto());
        edit_TXT_birthdate.setOnClickListener(view -> showDatePicker());

    }

    private void pickUpPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK){
            if (data == null) {
                //Display an error
                urlImage = MSPV.getInstance().getString(MSPV.USER_IMAGE,"");
                return;
            }
            try {
                Uri selectedImage = data.getData();
                Glide.with(EditProfileActivity.this)
                        .load(selectedImage)
                        .error(R.drawable.ic_profile)
                        .into(edit_profile_image);
                urlImage = selectedImage.toString();
                Log.d("editProfile",selectedImage.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showDatePicker() {
        hideSystemUI(this);
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int theme_id = R.style.MyDatePickerDialogTheme; // custom theme ID
        // creating a variable for date picker dialog.
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,theme_id,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // setting date to our edit text.
                        edit_TXT_birthdate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    }
                },
                // passing year, month and day for selected date in our date picker.
                year, month, day);
        // display our date picker dialog.
        datePickerDialog.show();
        datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, "Choose", datePickerDialog);
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#C6FB5800"));
        datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Cancel", datePickerDialog);
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#C6FB5800"));

    }

    private void finishCurrentActivity() {
        if(isFirstTime) {
            if(!updated){
                updateUser();
            }
            Intent intent = new Intent(this, NavigationActivity.class);
            startActivity(intent);
            finish();
        }else {
            finish();
            overridePendingTransition(R.anim.no_animation, R.anim.slide_down);
            onBackPressed();
        }
    }

    private void updateUser() {
        if(edit_TXT_first_name.getText().toString().isEmpty()){
            edit_TXT_first_name.setText("Runner");
        }
        if(edit_TXT_last_name.getText().toString().isEmpty()){
            edit_TXT_last_name.setText("Athlete");
        }
        MSPV.getInstance().putString(MSPV.USER_IMAGE,urlImage);
        MSPV.getInstance().putString(MSPV.USER_NAME,edit_TXT_first_name.getText().toString() + " " + edit_TXT_last_name.getText().toString());
        Runner newRunner = new Runner()
                .setFirstName(edit_TXT_first_name.getText().toString())
                .setLastName(edit_TXT_last_name.getText().toString())
                .setProfileImage(urlImage)
                .setCity(edit_TXT_city.getText().toString())
                .setState(edit_TXT_state.getText().toString())
                .setBio(edit_TXT_bio.getText().toString())
                .setBirthdate(edit_TXT_birthdate.getText().toString())
                .setGender(edit_SPINNER_gender.getSelectedItem().toString())
                .setWeight(edit_TXT_weight.getText().toString());
        FirebaseDatabaseManager.getInstance().addRunnerToDatabase(MSPV.getInstance().getString(MSPV.USERID,""), newRunner);
        updated = true;
        finishCurrentActivity();
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