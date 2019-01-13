package com.example.nemo1.weather21.model;

import com.example.nemo1.weather21.entity.Weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetWeatherAPI {
    @GET("current.json?key=a9919d781737410a90e72432180311")
    Call<Weather> getWeather(@Query("q") String q);
}
