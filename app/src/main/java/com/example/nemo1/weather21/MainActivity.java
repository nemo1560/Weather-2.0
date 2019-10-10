package com.example.nemo1.weather21;

import android.Manifest;
import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.nemo1.weather21.custom.CustomTextView;
import com.example.nemo1.weather21.custom.ScheduleUtils;
import com.example.nemo1.weather21.custom.Utility;
import com.example.nemo1.weather21.entity.Condition;
import com.example.nemo1.weather21.entity.Country;
import com.example.nemo1.weather21.entity.Current;
import com.example.nemo1.weather21.entity.Location;
import com.example.nemo1.weather21.model.Intents;
import com.example.nemo1.weather21.presenter.Presenter;
import com.example.nemo1.weather21.service.NotiService;
import com.example.nemo1.weather21.view.SendView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


public class MainActivity extends AppCompatActivity implements SendView, View.OnClickListener {
    private CustomTextView cloud,uv,currenttemp,time;
    private ImageView status,country,wall;
    ProgressBar loading;
    private Presenter presenter;
    private static String temp = "";
    private AlarmManager alarmManager;
    private Country countryInfo;
    private static final int REQUEST_LOCATION_PERMISSION = 100;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction() == Intents.NOTI){
                if(intent.getStringExtra("NOTI") == "restart"){
                    creatService();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        country = findViewById(R.id.country);
        cloud = findViewById(R.id.cloud);
        uv = findViewById(R.id.uv);
        currenttemp = findViewById(R.id.temp);
        status = findViewById(R.id.status);
        loading = findViewById(R.id.loading);
        time = findViewById(R.id.time);
        wall = findViewById(R.id.wall);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermission();
        initEvent();
    }

    private void notConnect() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Lỗi kết nối")
                .setMessage("Thiết bị của bạn chưa kết nối mạng")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create().show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isNetworkConnected()){
            loading.setVisibility(View.VISIBLE);
            getAPIWeather();
        }else {
            notConnect();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null;
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

    public void initEvent() {
        currenttemp.setOnClickListener(this);
        country.setOnClickListener(this);
        cloud.setOnClickListener(this);
    }

    //run present for process getAPI
    public void getAPIWeather(){
        presenter = new Presenter(this,this);
        presenter.process();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == currenttemp.getId()){
           if(isNetworkConnected()){
               loading.setVisibility(View.VISIBLE);
               getAPIWeather();
//               Toast.makeText(this,"Checking",Toast.LENGTH_SHORT).show();
           }else {
               notConnect();
           }
        }if(v.getId() == cloud.getId()){
//            countryInfo.getCapital();
            startActivity(new Intent("android.intent.action.Second"));
        }
    }

    @Override
    public void onViewLocation(Location location) {
        time.setText(location.getLocaltime());
    }

    @Override
    public void onViewCurrent(final Current current) {
        cloud.setText(current.getCloud());
        setUV(current.getUv());
        temp = current.getTemp_c();
        setTemp(temp);
        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WeatherInfoFragment weatherInfoFragment = WeatherInfoFragment.newInstance(current);
                weatherInfoFragment.show(getSupportFragmentManager(),"weatherInfo");
            }
        });
    }

    private void setTemp(String temp) {
        Double t = Double.valueOf(temp);
        if(t.intValue() < 10){
            currenttemp.setTextColor(Color.GRAY);
            wall.setImageResource(R.drawable.white);
        }else if(t.intValue() >= 10 && t.intValue() < 20){
            currenttemp.setTextColor(Color.BLUE);
            wall.setImageResource(R.drawable.blue);
        }else if(t.intValue() >= 20 && t.intValue() < 30){
            currenttemp.setTextColor(Color.GREEN);
            wall.setImageResource(R.drawable.green);
        }else if(t.intValue() >= 30 && t.intValue() < 34 ){
            currenttemp.setTextColor(Color.YELLOW);
            wall.setImageResource(R.drawable.yellow);
        }else if(t.intValue() >= 34 && t.intValue() < 41){
            currenttemp.setTextColor(Color.parseColor("#FFA500"));
            wall.setImageResource(R.drawable.orange);
        }else{
            currenttemp.setTextColor(Color.RED);
            wall.setImageResource(R.drawable.red);
        }
        currenttemp.setText(temp);
    }

    private void setUV(String UV) {
        Double v = Double.valueOf(UV);
        if(v.intValue() < 3){
            uv.setTextColor(Color.GREEN);
        }else if(v.intValue() >= 3 && v.intValue() < 6){
            uv.setTextColor(Color.YELLOW);
        }else if(v.intValue() > 5 && v.intValue() < 8){
            uv.setTextColor(Color.parseColor("#FFA500")); //orange
        }else if(v.intValue() >= 8 && v.intValue() < 11) {
            uv.setTextColor(Color.RED);
        }else {
            uv.setTextColor(Color.parseColor("#A65BA6")); //violet
        }
        uv.setText(UV);
    }

    //Tao service class
    public void creatService(){
        //Start jobSchedule để lặp lại service.
        if(Build.VERSION.SDK_INT ==Build.VERSION_CODES.P){
            ScheduleUtils.ScheduleUtils(getBaseContext());
        }else {
            Intent intent = new Intent(this,NotiService.class);
            startService(intent);
        }
    }

    private Bitmap img;
    @Override
    public void onViewCondition(Condition condition) {
        final String link = "http://" + condition.getIcon().substring(2,condition.getIcon().length());
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                InputStream in = null;
                try {
                    in = new URL(link).openStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                img = BitmapFactory.decodeStream(in);
                status.setImageBitmap(img);

            }
        });
    }

    private Bitmap flag;
    @Override
    public void getCountryInfo(final Country countryInfo) {
        this.countryInfo = countryInfo;
        final String link = "https://www.countryflags.io/"+countryInfo.getAlpha2Code()+"/flat/64.png";
        try {
            flag = BitmapFactory.decodeStream(new URL(link).openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        loading.setVisibility(View.INVISIBLE);
        country.setImageBitmap(flag);
        country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                flag.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                CountryInfoFragment countryInfoFragment = CountryInfoFragment.newInstance(countryInfo,byteArray);
                countryInfoFragment.show(getSupportFragmentManager(),"countryInfo");
            }
        });
    }

    @Override
    public void getError(String error) {
        Utility.Alert(this,"Lỗi",error).setNegativeButton("Cancel",null).create().show();
        loading.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Thoát")
                .setMessage("Bạn đồng thoát chương trình")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        creatService();
                        exit();
                    }
                })
                .setNegativeButton("Cancel",null);
        builder.create().show();
    }

    public void exit(){
        super.onBackPressed();
    }
}
