package com.example.nemo1.weather21.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.example.nemo1.weather21.entity.Country;
import com.example.nemo1.weather21.entity.Current;
import com.example.nemo1.weather21.entity.Location;
import com.example.nemo1.weather21.model.GetLocation;
import com.example.nemo1.weather21.model.Intents;
import com.example.nemo1.weather21.model.SendLocation;
import com.example.nemo1.weather21.model.SharedPreference;
import com.example.nemo1.weather21.service.WeatherService;
import com.example.nemo1.weather21.view.SendView;


public class Presenter implements SendLocation {
    private SendView sendView;
    private GetLocation getLocation;
    private Context context;
    private Location location;
    private Country country;
    private Current current;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction() == Intents.TEMP){
                if(intent.getStringExtra("value") == null){
                    location = (Location) intent.getSerializableExtra("location");
                    current = (Current) intent.getSerializableExtra("current");
                    country = (Country) intent.getSerializableExtra("country");
                    sendView.getCountryInfo(country);
                    sendView.onViewCurrent(current);
                    sendView.onViewLocation(location);
                }
                else {
                    String error = intent.getStringExtra("value");
                    sendView.getError(error);
                }
            }
        }
    };

    public Presenter(SendView sendView, Context context) {
        this.sendView = sendView;
        this.context = context;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intents.TEMP);
        context.registerReceiver(broadcastReceiver,intentFilter);

    }

    public void process() {
        getLocation = new GetLocation(context,this);
    }
    @Override
    public void onSendLocation(String location) {
        Log.d("location",location);
        new SharedPreference(context).edit().putString("location",location).apply();
        Intent intent = new Intent(context, WeatherService.class);
        context.startService(intent);
    }
}
