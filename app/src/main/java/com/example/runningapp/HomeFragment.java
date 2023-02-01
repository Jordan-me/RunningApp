package com.example.runningapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment {
    private RecyclerView home_LST_runs;
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
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        findViews(view);
        this.runs = new ArrayList<Run>();
        runAdapter = new RunAdapter(this,this.runs);
        initView(view);

        FirebaseDatabaseManager.getInstance().getRuns(new FirebaseDatabaseManager.OnRunsRetrievedCallback() {
            @Override
            public void onRunsRetrieved(ArrayList<Run> runsReturned) {
                ArrayList<Run> filteredRuns = (ArrayList<Run>) runsReturned.stream()
                        .filter(run -> run.isVisibleEveryone() == true)
                        .collect(Collectors.toList());

                runs.addAll(runsReturned);
                runAdapter.setRuns(filteredRuns);

            }
        });
        return view;
    }

    private void initView(View view) {
        home_LST_runs.setAdapter(runAdapter);
        home_LST_runs.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false));
    }


    private void findViews(View view) {
        home_LST_runs = view.findViewById(R.id.home_LST_runs);

    }

    @Override
    public void onStart() {
        super.onStart();
    }
}