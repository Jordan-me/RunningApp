package com.example.runningapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.stream.Collectors;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {
    private MaterialCardView profile_CARD_user;
    private CircleImageView profile_IMG_profile;
    private MaterialTextView profile_TXT_name;
    private MaterialButton profile_BTN_edit;
    private RecyclerView profile_LST_runs;
    private RunAdapter runAdapter;
    private ArrayList<Run> runs;

    private AppCompatActivity context;

    public void setActivity(AppCompatActivity activity) {
        this.context = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        findViews(view);
        initView(view);
        String uid = MSPV.getInstance().getString(MSPV.USERID,"");
        FirebaseDatabaseManager.getInstance().getRuns(new FirebaseDatabaseManager.OnRunsRetrievedCallback() {
            @Override
            public void onRunsRetrieved(ArrayList<Run> runsReturned) {
                ArrayList<Run> filteredRuns = (ArrayList<Run>) runsReturned.stream()
                        .filter(run -> run.getUserId().equals(uid))
                        .collect(Collectors.toList());

                runs.addAll(runsReturned);
                runAdapter.setRuns(filteredRuns);

            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Glide.with(this)
                .load( MSPV.getInstance().getString(MSPV.USER_IMAGE,""))
                .error(R.drawable.ic_profile)
                .into(profile_IMG_profile);
        String name =  MSPV.getInstance().getString(MSPV.USER_NAME,"");
        if (!name.isEmpty()){
            profile_TXT_name.setText(name);
        }
    }

    private void findViews(View view) {
        profile_CARD_user =  view.findViewById(R.id.profile_CARD_user);
        profile_IMG_profile =  view.findViewById(R.id.profile_IMG_profile);
        profile_TXT_name =  view.findViewById(R.id.profile_TXT_name);
        profile_LST_runs =  view.findViewById(R.id.profile_LST_runs);
        profile_BTN_edit =  view.findViewById(R.id.profile_BTN_edit);
    }

    private void initView(View view) {
        Glide.with(this)
                .load( MSPV.getInstance().getString(MSPV.USER_IMAGE,""))
                .error(R.drawable.ic_profile)
                .into(profile_IMG_profile);
        String name =  MSPV.getInstance().getString(MSPV.USER_NAME,"");
        if (!name.isEmpty()){
            profile_TXT_name.setText(name);
        }

        this.runs = new ArrayList<Run>();
        runAdapter = new RunAdapter(this,this.runs);
        profile_LST_runs.setAdapter(runAdapter);
        profile_LST_runs.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false));
        profile_BTN_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editProfile();
            }
        });
    }

    private void editProfile() {
        Intent intent = new Intent(context, EditProfileActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_up, R.anim.no_animation);
    }


}