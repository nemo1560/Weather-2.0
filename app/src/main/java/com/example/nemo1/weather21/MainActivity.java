package com.example.nemo1.weather21;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.nemo1.weather21.custom.AlarmUtils;
import com.example.nemo1.weather21.custom.CustomTextView;
import com.example.nemo1.weather21.entity.Condition;
import com.example.nemo1.weather21.entity.Current;
import com.example.nemo1.weather21.entity.Location;
import com.example.nemo1.weather21.model.Intents;
import com.example.nemo1.weather21.presenter.Presenter;
import com.example.nemo1.weather21.service.WeatherService;
import com.example.nemo1.weather21.view.SendView;


public class MainActivity extends AppCompatActivity implements SendView, View.OnClickListener {

    private CustomTextView name,country,cloud,uv,currenttemp;
    ProgressBar loading;
    private Presenter presenter;
    private static String temp = "";
    private AlarmManager alarmManager;
    private static final int REQUEST_LOCATION_PERMISSION = 100;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction() == Intents.TEMP){
                if(intent.getStringExtra("TEMP") != null){
                    temp = intent.getStringExtra("TEMP");
                    creatService();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name = findViewById(R.id.name);
        country = findViewById(R.id.country);
        cloud = findViewById(R.id.cloud);
        uv = findViewById(R.id.uv);
        currenttemp = findViewById(R.id.temp);
        loading = findViewById(R.id.loading);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermission();
        presenter = new Presenter(this,this);
        initEvent(currenttemp);
    }

    //Check permission GPS granted.
    public void checkPermission(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    checkPermission();
                }
                break;
            default:
                break;
        }
    }

    public void initEvent(CustomTextView currenttemp) {
        currenttemp.setOnClickListener(this);
        getAPIWeather();
        creatService();
    }

    //run present for process getAPI
    public void getAPIWeather(){
        presenter.process();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == currenttemp.getId()){
            loading.setVisibility(View.VISIBLE); //set loading animation when call API
            getAPIWeather();
            Toast.makeText(this,"Checking",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onViewLocation(Location location) {
        name.setText(location.getName());
        country.setText(location.getCountry());
    }

    @Override
    public void onViewCurrent(Current current) {
        loading.setVisibility(View.GONE);
        cloud.setText(current.getCloud());
        uv.setText(current.getUv());
        temp = current.getTemp_c();
        currenttemp.setText(temp);
    }

    //Tao service class
    public void creatService(){
        new AlarmUtils(this,alarmManager);
    }

    @Override
    public void onViewCondition(Condition condition) {

    }

    @Override
    protected void onDestroy() {
        registerReceiver(broadcastReceiver,new IntentFilter(Intents.TEMP));
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}
