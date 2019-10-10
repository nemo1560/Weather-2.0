package com.example.nemo1.weather21.custom;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nemo1.weather21.entity.Country;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ListCountry {
    private AppCompatActivity activity;

    public ListCountry(AppCompatActivity activity) {
        this.activity = activity;
    }

    public List<Country> getListCountry() throws IOException, JSONException {
        String content = "";
        List<Country> lst = new ArrayList<>();
        InputStream inputStream = activity.getAssets().open("country.txt");
        int size = inputStream.available();
        byte[] buffer = new byte[size];
        inputStream.read(buffer);
        inputStream.close();
        content = new String(buffer);
        JSONObject jsonObject = new JSONObject(content);

        return lst;
    }
}
