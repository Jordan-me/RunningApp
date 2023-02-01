package com.example.runningapp;

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

import java.util.ArrayList;

public class FirebaseDatabaseManager {

    private static FirebaseDatabaseManager firebaseDatabaseManager;
    private DatabaseReference mRootRef;
    private DatabaseReference mRunnersRef;
    private DatabaseReference mRunsRef;

    private FirebaseDatabaseManager() {
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mRunnersRef = mRootRef.child("runners");
        mRunsRef = mRootRef.child("runs");
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
    public void addRunnerToDatabase(String uid, Runner runner) {
        DatabaseReference runnerRef = mRunnersRef.child(uid);
        runnerRef.child("firstName").setValue(runner.getFirstName());
        runnerRef.child("lastName").setValue(runner.getLastName());
        runnerRef.child("profileImage").setValue(runner.getProfileImage());
        runnerRef.child("city").setValue(runner.getCity());
        runnerRef.child("state").setValue(runner.getState());
        runnerRef.child("bio").setValue(runner.getBio());
        runnerRef.child("birthdate").setValue(runner.getBirthdate());
        runnerRef.child("gender").setValue(runner.getGender());
        runnerRef.child("weight").setValue(runner.getWeight());
        runnerRef.child("phone").setValue(uid);
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
