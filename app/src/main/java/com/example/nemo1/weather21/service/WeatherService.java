package com.example.nemo1.weather21.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.nemo1.weather21.R;
import com.example.nemo1.weather21.entity.Condition;
import com.example.nemo1.weather21.entity.Current;
import com.example.nemo1.weather21.entity.Location;
import com.example.nemo1.weather21.model.Intents;
import com.example.nemo1.weather21.presenter.Presenter;
import com.example.nemo1.weather21.view.SendView;

public class WeatherService extends Service implements SendView {
    private String newTemp ="";
    private static String currentTemp = "";
    private final String CHANNEL_ID = "my_weather_01";
    private final String CHANNEL_NAME = "my_weather";
    private int NOTIFICATION_ID = 1;
    private NotificationManager notificationManager;
    private NotificationChannel notificationChannel;
    private Notification.Builder builder;
    private NotificationCompat.Builder cbuilder;
    private Thread thread;
    private Presenter presenter;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        startThread();
        getData();
        return START_STICKY;
    }

    private void getData() {
        presenter = new Presenter(WeatherService.this,getBaseContext());
        presenter.process();
        setNotification(newTemp);
    }

//    private void startThread() {
//        thread = new Thread(runnable);
//        thread.start();
//    }
//
//    private Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            while(true){
//                presenter = new Presenter(WeatherService.this,getBaseContext());
//                presenter.process();
//                Message msg = new Message();
//                Bundle data = new Bundle();
//                try {
//                    Log.d("serviceStatus",newTemp);
//                    data.putString("newTemp",newTemp);
//                    msg.setData(data);
//                    msg.arg1 = 1;
//                    mHandler.sendMessage(msg);
//                    Thread.sleep(30*60*1000);
//                } catch (InterruptedException e) {
//                    e.toString();
//                }
//            }
//        }
//    };
//
//    private Handler mHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if(msg.arg1 == 1){
//                Bundle bundle = msg.getData();
//                String temp = bundle.getString("newTemp");
//                setNotification(temp);
//            }
//        }
//    };

    public void send() {
        Intent intent = new Intent(Intents.TEMP);
        intent.putExtra("TEMP",newTemp);
        sendBroadcast(intent);
    }

    @Override
    public void onViewLocation(Location location) {

    }

    @Override
    public void onViewCurrent(Current current) {
        newTemp = current.getTemp_c();
    }

    @Override
    public void onViewCondition(Condition condition) {

    }

    public void setNotification(String temp){
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
                        .setChannelId(CHANNEL_ID)
                        .setPriority(Notification.PRIORITY_HIGH);
                notificationManager.notify(NOTIFICATION_ID,builder.build());
                Notification notification = builder.build();
                startForeground(1,notification);
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
                startForeground(2,new Notification());
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        send();
    }
}
