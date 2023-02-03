package com.example.runningapp;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class FirebaseDatabaseManager {

    private static FirebaseDatabaseManager firebaseDatabaseManager;
    private DatabaseReference mRootRef;
    private DatabaseReference mRunnersRef;
    private DatabaseReference mRunsRef;
    private FirebaseStorage storage;
    private StorageReference mStorageRef;

    private FirebaseDatabaseManager() {
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mRunnersRef = mRootRef.child("runners");
        mRunsRef = mRootRef.child("runs");
        storage = FirebaseStorage.getInstance();
        mStorageRef = storage.getReference();
    }
    public static FirebaseDatabaseManager getInstance() {
        return firebaseDatabaseManager;
    }
    public static FirebaseDatabaseManager initFirebaseDatabaseManager() {
        if (firebaseDatabaseManager == null) {
            firebaseDatabaseManager = new FirebaseDatabaseManager();
        }
        return firebaseDatabaseManager;
    }

    public void addRunnerToDatabase(FirebaseUser user) {
        String uid = user.getPhoneNumber();
        DatabaseReference runnerRef = mRunnersRef.child(uid);
        runnerRef.child("firstName").setValue("Runner");
        runnerRef.child("lastName").setValue("Athlete");
        runnerRef.child("phone").setValue(user.getPhoneNumber());
    }
    public void addRunnerToDatabase(String uid, Runner runner, byte[] imageData) {
        DatabaseReference runnerRef = mRunnersRef.child(uid);
        runnerRef.child("firstName").setValue(runner.getFirstName());
        runnerRef.child("lastName").setValue(runner.getLastName());
        if(imageData != null && imageData.length > 0){
            uploadImage(runnerRef,uid, imageData);
        }
//        runnerRef.child("profileImage").setValue(runner.getProfileImage());
        runnerRef.child("city").setValue(runner.getCity());
        runnerRef.child("state").setValue(runner.getState());
        runnerRef.child("bio").setValue(runner.getBio());
        runnerRef.child("birthdate").setValue(runner.getBirthdate());
        runnerRef.child("gender").setValue(runner.getGender());
        runnerRef.child("weight").setValue(runner.getWeight());
        runnerRef.child("phone").setValue(uid);
    }

    private void uploadImage(DatabaseReference runnerRef, String uid, byte[] imageData) {
        final StorageReference imagesRef = mStorageRef.child("images/" + uid);
        UploadTask uploadTask = imagesRef.putBytes(imageData);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String downloadUrl = uri.toString();
                        runnerRef.child("profileImage").setValue(downloadUrl);
                    }
                });
            }
        });
    }

    public void addRunToDatabase(Run run) {
        mRunsRef.child(run.getRunId()).setValue(run)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firebase", "Run saved successfully!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firebase", "Error saving run", e);
                    }
                });

    }

    public void getRunnerByUid(String uid, final FirebaseCallback callback) {
        mRunnersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                callback.onCallback(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onCallback(null);
            }
        });
    }

    public void getRuns(final OnRunsRetrievedCallback callback) {
        mRunsRef.orderByChild("date")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<Run> runs = new ArrayList<>();
                        if(snapshot.exists()){
                            for (DataSnapshot runSnapshot : snapshot.getChildren()){
                                Run run = runSnapshot.getValue(Run.class);
                                Log.d("Firebase Runs", run.toString());
                                if(run !=null && run.getRunId() != null){
                                    runs.add(run);
                                }
                            }
                            callback.onRunsRetrieved(runs);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    public interface FirebaseCallback {
        void onCallback(DataSnapshot dataSnapshot);

    }
    interface OnRunsRetrievedCallback {
        void onRunsRetrieved(ArrayList<Run> runs);
    }
}
