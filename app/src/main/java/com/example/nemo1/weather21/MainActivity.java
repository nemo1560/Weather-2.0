package com.example.nemo1.weather21;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;

import androidx.appcompat.app.AlertDialog;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.nemo1.weather21.custom.BaseActivity;
import com.example.nemo1.weather21.custom.CustomTextView;
import com.example.nemo1.weather21.custom.ScheduleUtils;
import com.example.nemo1.weather21.entity.Country;
import com.example.nemo1.weather21.entity.Current;
import com.example.nemo1.weather21.entity.Location;
import com.example.nemo1.weather21.model.GetLocation;
import com.example.nemo1.weather21.model.Intents;
import com.example.nemo1.weather21.model.SendLocation;
import com.example.nemo1.weather21.presenter.Presenter;
import com.example.nemo1.weather21.service.NotiService;
import com.example.nemo1.weather21.view.SendView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseActivity implements SendView, View.OnClickListener, SendLocation, OnMapReadyCallback {
    private CustomTextView cloud,uv,currenttemp,time,feel_temp,pressure;
    private ImageView country,wall;
    ProgressBar loading;
    private LatLng point;
    private Presenter presenter;
    private static String temp = "";
    private String coordinate;
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
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        country = findViewById(R.id.country);
        cloud = findViewById(R.id.cloud);
        uv = findViewById(R.id.uv);
        currenttemp = findViewById(R.id.temp);
        feel_temp = findViewById(R.id.feel_temp);
        loading = findViewById(R.id.loading);
        pressure = findViewById(R.id.pressure);
        time = findViewById(R.id.time);
        wall = findViewById(R.id.wall);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        List<String> reqPermissions = Arrays.asList(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.INTERNET);
        listPermissionRequest(reqPermissions);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initEvent();
    }

    private void notConnect() {
        Alert("Lỗi kết nối","Thiết bị của bạn chưa kết nối mạng");
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

    public void initEvent() {
        currenttemp.setOnClickListener(this);
        country.setOnClickListener(this);
        cloud.setOnClickListener(this);
    }

    //run present for process getAPI
    public void getAPIWeather(){
        new GetLocation(MainActivity.this,this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        if(point != null){
//            mMap.addMarker(new MarkerOptions().position(point).title("Your point"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
        }else {
            point = new LatLng(0,0);
//            mMap.addMarker(new MarkerOptions().position(point).title("Your point"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 13));
        }

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == currenttemp.getId()){
           if(isNetworkConnected()){
               loading.setVisibility(View.VISIBLE);
               getAPIWeather();
           }else {
               notConnect();
           }
        }if(v.getId() == cloud.getId()){
//            countryInfo.getCapital();
        }
    }

    @Override
    public void onViewLocation(Location location) {
        time.setText(location.getLocaltime());
    }

    private Bitmap img;
    @Override
    public void onViewCurrent(final Current current) {
        cloud.setText(current.getCloudcover());
        pressure.setText(current.getPressure()+" MB");
        setUV(current.getUv_index());
        setTemp(current.getTemperature()+"°",current.getFeelslike()+"°");
    }

    private void setTemp(String temp,String feel) {
        temp = temp.replace("°","");
        feel = feel.replace("°","");
        Double t = Double.valueOf(temp);
        Double feel_t = Double.valueOf(feel);
        if(t.intValue() < 10){
            currenttemp.setTextColor(Color.GRAY);
        }else if(t.intValue() >= 10 && t.intValue() < 20){
            currenttemp.setTextColor(Color.BLUE);
        }else if(t.intValue() >= 20 && t.intValue() < 30){
            currenttemp.setTextColor(Color.GREEN);
        }else if(t.intValue() >= 30 && t.intValue() < 34 ){
            currenttemp.setTextColor(Color.YELLOW);
        }else if(t.intValue() >= 34 && t.intValue() < 41){
            currenttemp.setTextColor(Color.parseColor("#FFA500"));
        }else{
            currenttemp.setTextColor(Color.RED);
        }

        if(feel_t.intValue() < 10){
            feel_temp.setTextColor(Color.GRAY);
        }else if(feel_t.intValue() >= 10 && feel_t.intValue() < 20){
            feel_temp.setTextColor(Color.BLUE);
        }else if(feel_t.intValue() >= 20 && feel_t.intValue() < 30){
            feel_temp.setTextColor(Color.GREEN);
        }else if(feel_t.intValue() >= 30 && feel_t.intValue() < 34 ){
            feel_temp.setTextColor(Color.YELLOW);
        }else if(feel_t.intValue() >= 34 && feel_t.intValue() < 41){
            feel_temp.setTextColor(Color.parseColor("#FFA500"));
        }else{
            feel_temp.setTextColor(Color.RED);
        }

        currenttemp.setText(temp+"°");
        feel_temp.setText(feel+"°");
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

    private Bitmap flag;
    @Override
    public void getCountryInfo(final Country countryInfo) {
        final String link = "https://www.countryflags.io/"+countryInfo.getAlpha2Code()+"/flat/64.png";
        new Runnable() {
            @Override
            public void run() {
                try {
                    flag = BitmapFactory.decodeStream(new URL(link).openConnection().getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.run();
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
        Alert("Lỗi",error);
        loading.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        Confirm("Thoát", "Bạn muốn thoát ứng dụng", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                creatService();
                exit();
            }
        });
    }

    @Override
    protected void onDestroy() {
        creatService();
        super.onDestroy();
    }

    public void exit(){
        super.onBackPressed();
    }

    @Override
    public void onSendLocation(String location) {
        coordinate = location;
        Log.d("location-main",coordinate);
        share().edit().putString("location",coordinate).apply();
        presenter = new Presenter(this,this);
        presenter.process();
    }

    @Override
    public void onSendLocationLatlng(LatLng latLng) {
        point = latLng;
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

}
