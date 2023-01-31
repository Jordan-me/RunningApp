package com.example.runningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class VerifyPhoneNumberActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    public static final String PHONE_NUMBER = "PHONE_NUMBER";
    private EditText verify_ETXT_digit_1;
    private EditText verify_ETXT_digit_2;
    private EditText verify_ETXT_digit_3;
    private EditText verify_ETXT_digit_4;
    private EditText verify_ETXT_digit_5;
    private EditText verify_ETXT_digit_6;
    private TextView verify_TXT_error;
    private EditText[] verificationCodeFields;
    private MaterialButton verify_BTN_verify;
    private String phoneNumber;
    private final int MAX_CODE_LENGTH = 6;

    private FirebaseAuth mAuth;
    private FirebaseAuthSettings firebaseAuthSettings;
    private String mVerificationId;
//    private String smsCode = "123456";
    private FirebaseDatabaseManager firebaseDatabaseManager;
    private boolean sent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone_number);
        hideSystemUI(this);
        Bundle extras = getIntent().getBundleExtra("Bundle");
        if (extras != null) {
            phoneNumber = extras.getString(PHONE_NUMBER);
        }
//        for testing
//        phoneNumber = "+972555555555";
        sent = false;
        findViews();
        initViews();
        activateSMSPermission();

    }

    private void activateSMSPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
        }else {
            // Permission has already been granted, proceed with the SMS related task
            // Send a verification code to the user's phone
            sent = true;
            sendVerificationCode();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission has been granted, proceed with the SMS related task
                    sendVerificationCode();
                } else {
                    // Permission has been denied, show a message to the user
                    Toast.makeText(this, "SMS permission is required to receive the verification code", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

// implementations of the callback functions that handle the results of the request
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d("VerificationLogs:", "onCodeSent:" + verificationId);
            super.onCodeSent(verificationId, token);
            // Save verification ID and resending token so we can use them later
            mVerificationId = verificationId;
    //            mResendToken = token;
        }
        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            if (credential != null) {
                Log.d("TAG", "Credential: " + credential.getSmsCode());
                Log.d("TAG", "Credential: " + credential.getProvider());
                Log.d("TAG", "Credential: " + credential.getSignInMethod());
            } else {
                Log.d("TAG", "Credential is null");
            }
            Log.d("VerificationLogs:", "onVerificationCompleted:" + credential);
//            Auto- fill the edit text
            String code = credential.getSmsCode();
            if(code!= null){
                autoCompleteEditTexts(code);
                credential = PhoneAuthProvider.getCredential(mVerificationId,code);
                signInWithPhoneAuthCredential(credential);

            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w("VerificationLogs:", "onVerificationFailed", e);
            String msg = e.getMessage();
            if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                msg = "Too many request enter with 123456";
            }
            // Show a message and update the UI
            Toast.makeText(VerifyPhoneNumberActivity.this,msg,Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeAutoRetrievalTimeOut(String phoneNumber) {
            super.onCodeAutoRetrievalTimeOut(phoneNumber);
            Log.d("VerificationLogs:", "onCodeAutoRetrievalTimeOut: Phone Number: " + phoneNumber);
        }

    };

    private void sendVerificationCode() {
        Log.d("ptttttttt_phone", phoneNumber);
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("VerificationLogs:", "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            MSPV.getInstance().putBoolean("isLoggedIn", true);
                            MSPV.getInstance().putString(MSPV.USERID,user.getPhoneNumber());
//                            firebaseDatabaseManager.addRunnerToDatabase(user);
//                            Intent intent = new Intent(VerifyPhoneNumberActivity.this,RunActivity.class);
//                            Intent intent = new Intent(VerifyPhoneNumberActivity.this,NavigationActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            Bundle bundle = new Bundle();
//                            bundle.putString(PHONE_NUMBER,phoneNumber);
//                            intent.putExtra("Bundle", bundle);
                            Intent intent = new Intent(VerifyPhoneNumberActivity.this,EditProfileActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(EditProfileActivity.IS_FIRST_TIME,true);
                            intent.putExtra("Bundle", bundle);
                            startActivity(intent);
                            finish();
                        }else{
                            // Sign in failed, display a message and update the UI
                            Log.d("VerificationLogs:", "signInWithCredential:failure", task.getException());
                            String msg = task.getException().getMessage();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                msg = "incorrect code";
                            }
                            Toast.makeText(VerifyPhoneNumberActivity.this,msg,Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void autoCompleteEditTexts(String code) {
        final Handler handler = new Handler();
        for (int i = 0; i < verificationCodeFields.length; i++) {
            final int finalI = i;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    verificationCodeFields[finalI].setText(Character.toString(code.charAt(finalI)));
                }
            }, i * 1000); // delay by 1 sec between each field
        }
    }

    private String getCodeFromEditTexts() {
        String code = "";
        for (int i = 0; i < verificationCodeFields.length; i++) {
            code += verificationCodeFields[i].getText();
        }
        return code;
    }

    private void initViews() {
        verificationCodeFields = new EditText[]{verify_ETXT_digit_1, verify_ETXT_digit_2, verify_ETXT_digit_3, verify_ETXT_digit_4, verify_ETXT_digit_5, verify_ETXT_digit_6};
        verify_BTN_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = getCodeFromEditTexts();
                if(code.isEmpty() || code.length() < MAX_CODE_LENGTH){
                    verify_TXT_error.setText("* Enter valid code..");
                    verify_TXT_error.setError("");
                    verify_TXT_error.setVisibility(View.VISIBLE);
                    verify_ETXT_digit_1.requestFocus();
                    return;
                }
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,code);
                signInWithPhoneAuthCredential(credential);
            }
        });
        verify_ETXT_digit_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verify_TXT_error.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void findViews() {
        mAuth = FirebaseAuth.getInstance();
        firebaseAuthSettings = mAuth.getFirebaseAuthSettings();
        firebaseDatabaseManager = FirebaseDatabaseManager.getInstance();
//        firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(phoneNumber,getRandomNumberString());
//        firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(phoneNumber, smsCode);
        verify_ETXT_digit_1 = findViewById(R.id.verify_ETXT_digit_1);
        verify_ETXT_digit_2 = findViewById(R.id.verify_ETXT_digit_2);
        verify_ETXT_digit_3 = findViewById(R.id.verify_ETXT_digit_3);
        verify_ETXT_digit_4 = findViewById(R.id.verify_ETXT_digit_4);
        verify_ETXT_digit_5 = findViewById(R.id.verify_ETXT_digit_5);
        verify_ETXT_digit_6 = findViewById(R.id.verify_ETXT_digit_6);
        verify_BTN_verify = findViewById(R.id.verify_BTN_verify);
        verify_TXT_error = findViewById(R.id.verify_TXT_error);
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
        if(!sent){
            activateSMSPermission();
        }
    }

}