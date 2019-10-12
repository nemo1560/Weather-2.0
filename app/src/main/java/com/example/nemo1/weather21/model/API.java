package com.example.nemo1.weather21.model;


import android.content.Context;
import android.util.Log;

import com.example.nemo1.weather21.entity.Current;
import com.example.nemo1.weather21.entity.Location;
import com.example.nemo1.weather21.entity.Weather;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class API {
    private SendPresenter sendPresenter;
    private Context context;

    public API(SendPresenter sendPresenter , Context context) {
        this.sendPresenter = sendPresenter;
        this.context = context;
    }

    public void LoadAPI(String location) {
        GetWeatherAPI getWeatherAPI = RetrofitInstance.RetrofitInstance().create(GetWeatherAPI.class);
        Call<Weather> weatherCall = getWeatherAPI.getWeather(location);
        weatherCall.enqueue(new Callback<Weather>() {
            @Override
            public void onResponse(Call<Weather> call, Response<Weather> response) {
                Current current = response.body().getCurrent();
                Location location = response.body().getLocation();
                sendPresenter.getCurrentsuccessfully(current);
                sendPresenter.getLocationsuccessfully(location);
            }
            @Override
            public void onFailure(Call<Weather> call, Throwable t) {
                Log.d("checkAPI",t.getMessage());
            }
        });
    }
}
