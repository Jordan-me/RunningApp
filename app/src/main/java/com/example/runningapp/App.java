package com.example.runningapp;

import android.app.Application;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        MSPV.initMSPV(this);
        FirebaseDatabaseManager.initFirebaseDatabaseManager();
    }

}
