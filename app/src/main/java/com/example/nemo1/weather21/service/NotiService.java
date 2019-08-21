package com.example.nemo1.weather21.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.nemo1.weather21.R;
import com.example.nemo1.weather21.entity.Condition;
import com.example.nemo1.weather21.entity.Country;
import com.example.nemo1.weather21.entity.Current;
import com.example.nemo1.weather21.entity.Location;
import com.example.nemo1.weather21.model.GetLocation;
import com.example.nemo1.weather21.model.Intents;
import com.example.nemo1.weather21.model.OkHttp;
import com.example.nemo1.weather21.model.SendLocation;
import com.example.nemo1.weather21.model.SharedPreference;
import com.example.nemo1.weather21.model.URLs;
import com.example.nemo1.weather21.presenter.Presenter;
import com.example.nemo1.weather21.view.SendView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NotiService extends Service implements SendLocation {
    private String coordinates ="";
    private static String currentTemp = "";
    private final String CHANNEL_ID = "my_weather_01";
    private final String CHANNEL_NAME = "my_weather";
    private int NOTIFICATION_ID = 1;
    private NotificationManager notificationManager;
    private NotificationChannel notificationChannel;
    private Notification.Builder builder;
    private NotificationCompat.Builder cbuilder;
    private Thread thread;
    private Location location;
    private Current current;
    private Condition condition;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startThread();
//        getData();
        return START_STICKY;
    }

    private void startThread() {
        thread = new Thread(runnable);
        thread.start();
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while(true){
                coordinates = new SharedPreference(getBaseContext()).init().getString("location","0");
                getWeatherData(coordinates);
                Message msg = new Message();
                Bundle data = new Bundle();
                try {
//                    Log.d("serviceStatus",current.getTemp_c());
                    data.putString("newTemp",current.getTemp_c());
                    msg.setData(data);
                    msg.arg1 = 1;
                    mHandler.sendMessage(msg);
                    Thread.sleep(60*60*1000);
                } catch (InterruptedException e) {
                    e.toString();
                }
            }
        }
    };

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.arg1 == 1){
                Bundle bundle = msg.getData();
                String temp = bundle.getString("newTemp");
                setNotification(temp);
                send();
            }
        }
    };

    public void send() {
        Intent intent = new Intent(Intents.NOTI);
        intent.putExtra("NOTI",current.getTemp_c());
        sendBroadcast(intent);
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

    protected void getWeatherData(String coordinates) {
        location = new Location();
        current = new Current();
        condition = new Condition();
        String key = "a9919d781737410a90e72432180311";
        Map<String, String> paramaters = new HashMap<String, String>();
        paramaters.put("key",key);
        paramaters.put("q",coordinates);
        try {
            String json = OkHttp.getOKHttp(URLs.URLWEATHER,paramaters);
            if(json != null && json.length() > 0){
                JSONObject jsonObject = new JSONObject(json);
//
//                JSONObject locationObj = jsonObject.getJSONObject("location");
//                location.setName(locationObj.getString("name"));
//                location.setRegion(locationObj.getString("region"));
//                location.setCountry(locationObj.getString("country"));
//                location.setLat(locationObj.getString("lat"));
//                location.setLon(locationObj.getString("lon"));
//                location.setTz_id(locationObj.getString("tz_id"));
//                location.setLocaltime_epoch(locationObj.getString("localtime_epoch"));
//                location.setLocaltime(locationObj.getString("localtime"));

                JSONObject currentObj = jsonObject.getJSONObject("current");
                current.setLast_updated_epoch(currentObj.getString("last_updated_epoch"));
                current.setLast_updated(currentObj.getString("last_updated"));
                current.setTemp_c(currentObj.getString("temp_c"));
                current.setTemp_f(currentObj.getString("temp_f"));
                current.setIs_day(currentObj.getString("is_day"));
                current.setWind_mph(currentObj.getString("wind_mph"));
                current.setWind_kph(currentObj.getString("wind_kph"));
                current.setWind_degree(currentObj.getString("wind_degree"));
                current.setWind_dir(currentObj.getString("wind_dir"));
                current.setPressure_mb(currentObj.getString("pressure_mb"));
                current.setPressure_in(currentObj.getString("pressure_in"));
                current.setPrecip_mm(currentObj.getString("precip_mm"));
                current.setPrecip_in(currentObj.getString("precip_in"));
                current.setHumidity(currentObj.getString("humidity"));
                current.setCloud(currentObj.getString("cloud"));
                current.setFeelslike_c(currentObj.getString("feelslike_c"));
                current.setFeelslike_f(currentObj.getString("feelslike_f"));
                current.setVis_km(currentObj.getString("vis_km"));
                current.setVis_miles(currentObj.getString("vis_miles"));
                current.setUv(currentObj.getString("uv"));

//                JSONObject conditionObj = currentObj.getJSONObject("condition");
//                condition.setText(conditionObj.getString("text"));
//                condition.setIcon(conditionObj.getString("icon"));
//                condition.setCode(conditionObj.getString("code"));

                send();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSendLocation(String location) {
        coordinates = location;
    }
}
