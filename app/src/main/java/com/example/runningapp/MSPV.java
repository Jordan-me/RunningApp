package com.example.runningapp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * class that manage the shared preference file
 */
public class MSPV {
    public static final String USER_NAME = "USER_NAME" ;
    private final String SP_FILE = "SP_FILE_RUNNING_APP";
    private static MSPV mspv;
    private SharedPreferences sharedPreferences;
    public static String USERID = "PHONE_NUMBER";
    public static String USER_IMAGE = "USER_IMAGE";

    public static MSPV getInstance() {
        return mspv;
    }

    private MSPV(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(SP_FILE, Context.MODE_PRIVATE);
    }

    public static MSPV initMSPV(Context context) {
        if (mspv == null) {
            mspv = new MSPV(context);
        }
        return mspv;
    }

    public String getString(String KEY, String defValue) {
        if(sharedPreferences==null){
            return null;
        }else
            return sharedPreferences.getString(KEY, defValue);
    }

    public void putString(String KEY, String value) {
        sharedPreferences.edit().putString(KEY, value).apply();
    }
    public boolean getBoolean(String KEY, boolean defValue) {
        return sharedPreferences.getBoolean(KEY, defValue);
    }

    public void putBoolean(String KEY, boolean value) {
        sharedPreferences.edit().putBoolean(KEY, value).apply();
    }
    public int getInt(String KEY, int defValue) {
        return sharedPreferences.getInt(KEY, defValue);
    }

    public void putInt(String KEY, int value) {
        sharedPreferences.edit().putInt(KEY, value).apply();
    }

    public void putDouble(String KEY, double defValue) {
        putString(KEY, String.valueOf(defValue));
    }

    public double getDouble(String KEY, double defValue) {
        return Double.parseDouble(getString(KEY, String.valueOf(defValue)));
    }
}
