package com.example.nemo1.weather21.custom;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.nemo1.weather21.service.NotiService;


public class AlarmUtils {
    private Context context;
    private Intent intentService;
    private AlarmManager alarmManager;

    public AlarmUtils(Context context, AlarmManager alarmManager) {
        this.context = context;
        this.alarmManager = alarmManager;
        intentService = new Intent(context, NotiService.class);
        startAlarm();
    }

    private void startAlarm() {
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intentService, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),pendingIntent);
            Log.d("currentTime",System.currentTimeMillis()+"");
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
            Log.d("currentTime",System.currentTimeMillis()+"");
        }
    }
}

