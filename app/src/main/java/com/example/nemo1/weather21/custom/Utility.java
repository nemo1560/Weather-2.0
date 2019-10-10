package com.example.nemo1.weather21.custom;

import android.app.AlertDialog;
import android.content.Context;

public class Utility {

    public static AlertDialog.Builder Alert(Context context,String title,String content){
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(content);
        return builder;
    }
}
