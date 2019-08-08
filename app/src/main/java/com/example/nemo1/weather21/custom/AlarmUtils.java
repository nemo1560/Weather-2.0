package com.example.nemo1.weather21.custom;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.example.nemo1.weather21.service.WeatherService;


public class AlarmUtils {
    private Context context;
    private Intent intentService;
    private AlarmManager alarmManager;

    private int minute = 5*60*1000;

    public AlarmUtils(Context context, AlarmManager alarmManager) {
        this.context = context;
        this.alarmManager = alarmManager;
        intentService = new Intent(context, WeatherService.class);
        startAlarm();
    }

    private void startAlarm() {
        PendingIntent pendingIntent = null;
        pendingIntent = PendingIntent.getService(context, 0, intentService, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),minute, pendingIntent); ;
        } else {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),minute, pendingIntent);
        }
    }
}

