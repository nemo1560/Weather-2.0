package com.example.nemo1.weather21;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.nemo1.weather21.custom.CustomTextView;
import com.example.nemo1.weather21.entity.Condition;
import com.example.nemo1.weather21.entity.Current;
import com.example.nemo1.weather21.entity.Location;
import com.example.nemo1.weather21.presenter.Presenter;
import com.example.nemo1.weather21.service.WeatherService;
import com.example.nemo1.weather21.view.SendView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements SendView, View.OnClickListener {
    @BindView(R.id.name)
    CustomTextView name;
    @BindView(R.id.country)
    CustomTextView country;
    @BindView(R.id.cloud)
    CustomTextView cloud;
    @BindView(R.id.uv)
    CustomTextView uv;
    @BindView(R.id.temp)
    CustomTextView currenttemp;
    @BindView(R.id.loading)
    ProgressBar loading;
    private Presenter presenter;
    private static String temp = "";
    private Intent intentService;
    private static final int REQUEST_LOCATION_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermission();
        presenter = new Presenter(this, loading,this);
        initEvent(currenttemp);
    }

    //Check permission GPS granted.
    public void checkPermission(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
    }

    public void getAPIWeather(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                presenter.process();
                handler.postDelayed(this,7200000);//7200000
            }
        },500);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == currenttemp.getId()){
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
        cloud.setText(current.getCloud());
        uv.setText(current.getUv());
        temp = current.getTemp_c();
        currenttemp.setText(temp);
        creatService();
    }

    //Tao service class
    public void creatService(){
        intentService = new Intent(this,WeatherService.class);
        intentService.putExtra("temp",temp);
        startService(intentService);
    }

    @Override
    public void onViewCondition(Condition condition) {

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(intentService);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Toast.makeText(this,"Chuong trinh se tu dong cap nhat thoi tiet",Toast.LENGTH_SHORT).show();
        presenter.process();
    }
}
