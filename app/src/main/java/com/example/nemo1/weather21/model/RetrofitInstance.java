package com.example.nemo1.weather21.model;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//Lop singleton retrofit object
public class RetrofitInstance {
    private static Retrofit retrofit; //tao retrofit object
    private static final String API = "http://api.apixu.com/v1/";

    //Goi lai class retrofitInstance
    public static Retrofit RetrofitInstance() {
        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                            .baseUrl(API)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
        }
        return retrofit;
    }
}
