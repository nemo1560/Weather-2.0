package com.example.nemo1.weather21.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import com.example.nemo1.weather21.R;

import androidx.core.app.NotificationCompat;

public class WeatherService extends Service{
    private String temp ="";
    private final String CHANNEL_ID = "my_weather_01";
    private final String CHANNEL_NAME = "my_weather";
    private int NOTIFICATION_ID = 1;
    private NotificationManager notificationManager;
    private NotificationChannel notificationChannel;
    private Notification.Builder builder;
    private NotificationCompat.Builder cbuilder;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setNotification(intent);
        return START_STICKY;
    }

    public void setNotification(Intent intent){
        temp = intent.getStringExtra("temp");
        if(!temp.isEmpty()){
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            ////notification cho dong anroid 8.0
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                notificationChannel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.enableLights(true);
                notificationChannel.enableVibration(true);
                notificationChannel.setLightColor(android.R.color.holo_red_dark);
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

                notificationManager.createNotificationChannel(notificationChannel);

                builder = new Notification.Builder(this, CHANNEL_ID)
                        .setContentTitle("Weather")
                        .setContentText(temp)
                        .setSmallIcon(R.mipmap.weather_local)
                        .setAutoCancel(true)
                        .setChannelId(CHANNEL_ID);
                notificationManager.notify(NOTIFICATION_ID,builder.build());
            }
            else {
                //notification cho dong anroid thap hon 8.0
                cbuilder = new NotificationCompat.Builder(this);
                cbuilder.setContentTitle("Weather")
                        .setContentText(temp)
                        .setSmallIcon(R.mipmap.weather_local)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setAutoCancel(true);
                notificationManager.notify(NOTIFICATION_ID,cbuilder.build());
            }
        }
    }
}
