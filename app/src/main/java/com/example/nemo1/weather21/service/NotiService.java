package com.example.nemo1.weather21.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import com.example.nemo1.weather21.MainActivity;
import com.example.nemo1.weather21.R;
import com.example.nemo1.weather21.custom.ScheduleUtils;
import com.example.nemo1.weather21.entity.Condition;
import com.example.nemo1.weather21.entity.Current;
import com.example.nemo1.weather21.entity.Location;
import com.example.nemo1.weather21.model.Intents;
import com.example.nemo1.weather21.model.OkHttp;
import com.example.nemo1.weather21.model.SendLocation;
import com.example.nemo1.weather21.model.SharedPreference;
import com.example.nemo1.weather21.model.URLs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NotiService extends Service implements SendLocation, signal {
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
    private Condition condition;
    private signal signal;
    private FirebaseDatabase database;

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
                    data.putString("newTemp", current.getTemp_c() /*"Kiểm tra nhiệt độ"*/);
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
                        .setContentTitle("Thông tin thời tiết")
                        .setContentText("Nhiệt độ: "+temp+", Áp suất: "+current.getPressure_mb())
                        .setSmallIcon(R.mipmap.weather_local)
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
                cbuilder.setContentTitle("Thông tin thời tiết")
                        .setContentText("Nhiệt độ: "+temp+", Áp suất: "+current.getPressure_mb())
                        .setSmallIcon(R.mipmap.weather_local)
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
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean anonymous;
    private void sendFirebase(String temp, signal signal) {
        this.signal = signal;
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                FirebaseUser user = mAuth.getCurrentUser();
                anonymous = user.isAnonymous();
            }
        });
        if(anonymous){
            signal.setValue(temp);
        }
    }
    private void readFirebase(){
        final DatabaseReference myRef = database.getReference("notifi");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String temp = dataSnapshot.getValue(String.class);
//                setNotification(temp, currentTime);
//                myRef.setValue("");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //restart service
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
    public void setValue(String temp) {
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("notifi");
        myRef.setValue(temp);
//        readFirebase();
    }
}

interface signal{
    void setValue(String temp);
}
