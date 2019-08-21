package com.example.nemo1.weather21.model;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreference {
    private SharedPreferences sharedPreference;
    private SharedPreferences.Editor editor;
    private Context context;

    public SharedPreference(Context context) {
        this.context = context;
    }

    public SharedPreferences init(){
        sharedPreference = context.getSharedPreferences("weather",0);
        return sharedPreference;
    }

    public SharedPreferences.Editor edit(){
        editor = init().edit();
        return editor;
    }
}
