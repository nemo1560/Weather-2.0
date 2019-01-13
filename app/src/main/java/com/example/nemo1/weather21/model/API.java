package com.example.nemo1.weather21.model;


import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.nemo1.weather21.entity.Condition;
import com.example.nemo1.weather21.entity.Current;
import com.example.nemo1.weather21.entity.Location;
import com.example.nemo1.weather21.entity.Weather;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class API {
    private SendPresenter sendPresenter;
    private ProgressBar loading;
    private GetLocation getLocation;
    private Context context;

    public API(SendPresenter sendPresenter, ProgressBar loading, Context context) {
        this.loading = loading;
        this.sendPresenter = sendPresenter;
        this.context = context;
    }

    public void LoadAPI(String location) {
        loading.setVisibility(View.VISIBLE); //set loading animation when call API
        GetWeatherAPI getWeatherAPI = RetrofitInstance.RetrofitInstance().create(GetWeatherAPI.class);
        Call<Weather> weatherCall = getWeatherAPI.getWeather(location);
        weatherCall.enqueue(new Callback<Weather>() {
            @Override
            public void onResponse(Call<Weather> call, Response<Weather> response) {
                Current current = response.body().getCurrent();
                Location location = response.body().getLocation();
                Condition condition = response.body().getCurrent().getCondition();
                sendPresenter.getConditionsuccessfully(condition);
                sendPresenter.getCurrentsuccessfully(current);
                sendPresenter.getLocationsuccessfully(location);
                loading.setVisibility(View.GONE);
            }
            @Override
            public void onFailure(Call<Weather> call, Throwable t) {
                Log.d("checkAPI",t.getMessage());
            }
        });
    }
}
