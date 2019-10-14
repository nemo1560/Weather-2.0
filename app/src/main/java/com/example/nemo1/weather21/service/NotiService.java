package com.example.nemo1.weather21.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import android.util.Log;


import com.example.nemo1.weather21.MainActivity;
import com.example.nemo1.weather21.R;
import com.example.nemo1.weather21.custom.ScheduleUtils;
import com.example.nemo1.weather21.entity.Current;
import com.example.nemo1.weather21.entity.Location;
import com.example.nemo1.weather21.model.Intents;
import com.example.nemo1.weather21.model.OkHttp;
import com.example.nemo1.weather21.model.SendLocation;
import com.example.nemo1.weather21.model.SharedPreference;
import com.example.nemo1.weather21.model.URLs;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NotiService extends Service implements SendLocation {
    private String coordinates ="";
    private static String currentTemp = "";
    private final String CHANNEL_ID = "my_weather_01";
    private final String CHANNEL_NAME = "my_weather";
    private final static int NOTIFICATION_ID = 1;
    private NotificationManager notificationManager;
    private NotificationChannel notificationChannel;
    private Notification.Builder builder;
    private NotificationCompat.Builder cbuilder;
    private Thread thread;
    private Location location;
    private Current current;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startThread();
        Log.d("checkStatus","OK");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startThread() {
        thread = new Thread(runnable);
        thread.start();
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while(true){
                coordinates = new SharedPreference(getBaseContext()).init().getString("location", "0");
                getWeatherData(coordinates);
                Message msg = new Message();
                Bundle data = new Bundle();
                try {
                    Log.d("WEATHER","@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                    data.putString("newTemp", current.getTemperature() /*"Kiểm tra nhiệt độ"*/);
                    msg.setData(data);
                    msg.arg1 = 1;
                    mHandler.sendMessage(msg);
                    Thread.sleep(60 * 60 * 1000);
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
//                sendFirebase(temp,NotiService.this);
                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                setNotification(temp,currentTime);
            }
        }
    };

    public void send() {
        Intent intent = new Intent(Intents.NOTI);
        intent.putExtra("NOTI","restart");
        sendBroadcast(intent);
    }

    public void setNotification(String temp, String currentTime){
        if(!temp.isEmpty()){
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            ////notification cho dong anroid 8.0
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                notificationChannel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.enableLights(true);
                notificationChannel.enableVibration(true);
                notificationChannel.setLightColor(Color.GREEN);
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

                notificationManager.createNotificationChannel(notificationChannel);

                builder = new Notification.Builder(this, CHANNEL_ID)
                        .setContentTitle("Nhiệt độ: "+temp+"°C")
                        .setContentText("Áp suất: "+current.getPressure())
                        .setSmallIcon(Icon.createWithBitmap(textAsBitmap(temp+"°C", (float) 100,Color.WHITE)))
                        .setAutoCancel(true)
                        .setChannelId(CHANNEL_ID)
                        .setOngoing(false)
                        .setPriority(Notification.PRIORITY_HIGH);

                Intent notificationIntent = new Intent(getApplication(), MainActivity.class);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent intent = PendingIntent.getActivity(getApplication(), 0, notificationIntent, 0);
                builder.setContentIntent(intent);

                Notification notification = builder.build();

                //bật startforcegound notification.
//                startForeground(1,notification);

                notificationManager.notify(NOTIFICATION_ID,notification);

            }
            else {
                //notification cho dong anroid thap hon 8.0
                cbuilder = new NotificationCompat.Builder(this);
                cbuilder.setContentTitle("Nhiệt độ: "+temp+"°C")
                        .setContentText("Áp suất: "+current.getPressure())
                        .setSmallIcon(R.mipmap.weather_local)
                        .setLargeIcon(textAsBitmap(temp+"°C", (float) 100,Color.BLACK))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setLights(Color.parseColor("#46d3a0"), 500, 2000)
                        .setAutoCancel(true);

                Intent notificationIntent = new Intent(getApplication(), MainActivity.class);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent intent = PendingIntent.getActivity(getApplication(), 0, notificationIntent, 0);
                cbuilder.setContentIntent(intent);

                Notification notification = cbuilder.build();
                notificationManager.notify(NOTIFICATION_ID,notification);
            }
        }
    }

    public Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    protected void getWeatherData(String coordinates) {
        location = new Location();
        current = new Current();
        String key = "ae17c23c4fa107d50a4a73373c2517ff";
        Map<String, String> paramaters = new HashMap<String, String>();
        paramaters.put("access_key",key);
        paramaters.put("query",coordinates);
        try {
            String json = OkHttp.getOKHttp(URLs.URLWEATHER,paramaters);
            if(json != null && json.length() > 0){
                JSONObject jsonObject = new JSONObject(json);

                JSONObject currentObj = jsonObject.getJSONObject("current");
                current.setTemperature(currentObj.getString("temperature"));
                current.setIs_day(currentObj.getString("is_day"));
                current.setWind_speed(currentObj.getString("wind_speed"));
                current.setWind_degree(currentObj.getString("wind_degree"));
                current.setWind_dir(currentObj.getString("wind_dir"));
                current.setPressure(currentObj.getString("pressure"));
                current.setHumidity(currentObj.getString("humidity"));
                current.setCloudcover(currentObj.getString("cloudcover"));
                current.setUv_index(currentObj.getString("uv_index"));
                current.setFeelslike(currentObj.getString("feelslike"));
                current.setVisibility(currentObj.getString("visibility"));
                current.setWeather_icons(currentObj.getJSONArray("weather_icons").getString(0));
                current.setWeather_descriptions(currentObj.getJSONArray("weather_descriptions").getString(0));

            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    //restart service
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d("STOP","00000000000000000");
        onCreate();
        ScheduleUtils.ScheduleUtils(getApplicationContext());
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d("STOP","00000000000000000");
        onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("STOP","00000000000000000");
        onCreate();
    }

    @Override
    public void onSendLocation(String location) {
        coordinates = location;
    }

    @Override
    public void onSendLocationLatlng(LatLng latLng) {

    }

}

