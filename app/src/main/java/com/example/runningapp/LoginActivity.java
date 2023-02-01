package com.example.runningapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import com.google.android.material.button.MaterialButton;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;


public class LoginActivity extends AppCompatActivity {
    private EditText login_ETXT_phoneNumber;
    private MaterialButton login_BTN_submit;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        hideSystemUI(this);
        findViews();
        initViews();

    }

    private void initViews() {
        login_BTN_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyPhoneNumber(view);
            }
        });

    }

    private void findViews() {
        login_ETXT_phoneNumber = findViewById(R.id.login_ETXT_phoneNumber);
        login_BTN_submit = findViewById(R.id.login_BTN_submit);
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
    public void verifyPhoneNumber(View view) {
        // TODO: 18/01/2023 create country code picker
        phoneNumber = login_ETXT_phoneNumber.getText().toString();
        if(!phoneNumber.contains("+972")){
            phoneNumber = "+972" + phoneNumber.substring(1);
        }
        if(!isValidPhoneNumber(phoneNumber)){
            login_ETXT_phoneNumber.setError("Valid phone number is required");
            login_ETXT_phoneNumber.requestFocus();
            return;
        }
        Intent intent = new Intent(this, VerifyPhoneNumberActivity.class );
        Bundle bundle = new Bundle();
        bundle.putString(VerifyPhoneNumberActivity.PHONE_NUMBER,phoneNumber);
        intent.putExtra("Bundle", bundle);
        startActivity(intent);
    }

    public boolean isValidPhoneNumber(String phoneNumber) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber numberProto = null;
        try {
            numberProto = phoneUtil.parse(phoneNumber, "US");
        } catch (NumberFormatException | NumberParseException e) {
            return false;
        }
        return phoneUtil.isValidNumber(numberProto);
    }


}