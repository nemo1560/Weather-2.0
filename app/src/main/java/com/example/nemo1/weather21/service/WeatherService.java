package com.example.nemo1.weather21.service;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.example.nemo1.weather21.entity.Country;
import com.example.nemo1.weather21.entity.Current;
import com.example.nemo1.weather21.entity.Location;
import com.example.nemo1.weather21.model.HttpUtils;
import com.example.nemo1.weather21.model.Intents;
import com.example.nemo1.weather21.model.OkHttp;
import com.example.nemo1.weather21.model.SharedPreference;
import com.example.nemo1.weather21.model.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@SuppressLint("NewApi")
public class WeatherService extends IntentService {
    private String json;
    private Location location;
    private Current current;
    private Country country;


    public WeatherService() {
        super("WeatherService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        getWeatherData(intent);
    }

    private void send(String value){
        Intent intent = new Intent(Intents.TEMP);
        if(value != null){
            intent.putExtra("value",value);
        }else {
            intent.putExtra("location",location);
            intent.putExtra("current",current);
            intent.putExtra("country",country);
        }
        sendBroadcast(intent);
    }

    protected void getWeatherData(Intent intent) {
        location = new Location();
        current = new Current();
        String coordinates = new SharedPreference(getApplicationContext()).init().getString("location","0");
        Map<String, String> paramaters = new HashMap<>();
        paramaters.put("access_key",URLs.KeyAPI);
        paramaters.put("query",coordinates);
        try {
            json = OkHttp.getOKHttp(URLs.URLWEATHER,paramaters);
            if(json != null && json.length() > 0){
                Log.d("currentLog",json);
                JSONObject jsonObject = new JSONObject(json);
                JSONObject locationObj = jsonObject.getJSONObject("location");
                location.setName(locationObj.getString("name"));
                location.setRegion(locationObj.getString("region"));
                location.setCountry(locationObj.getString("country"));
                location.setLat(locationObj.getString("lat"));
                location.setLon(locationObj.getString("lon"));
                location.setTz_id(locationObj.getString("timezone_id"));
                location.setLocaltime_epoch(locationObj.getString("localtime_epoch"));
                location.setLocaltime(locationObj.getString("localtime"));

                JSONObject currentObj = jsonObject.getJSONObject("current");
                current.setTemperature(currentObj.getString("temperature"));
                current.setIs_day(currentObj.getString("is_day"));
                current.setWind_speed(currentObj.getString("wind_speed"));
                current.setWind_degree(currentObj.getString("wind_degree"));
                current.setWind_dir(currentObj.getString("wind_dir"));
                current.setPressure(currentObj.getString("pressure"));
                current.setHumidity(currentObj.getString("humidity"));
                current.setCloudcover(currentObj.getString("cloudcover"));
                current.setUv_index(currentObj.getString("uv_index"));
                current.setFeelslike(currentObj.getString("feelslike"));
                current.setVisibility(currentObj.getString("visibility"));
                current.setWeather_icons(currentObj.getJSONArray("weather_icons").getString(0));
                current.setWeather_descriptions(currentObj.getJSONArray("weather_descriptions").getString(0));
                getCountryData();

        }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            /*send(e.toString());*/
        }
    }

    protected void getCountryData() {
        country = new Country();
        String json;
        Map<String,String> parameters = new HashMap<>();
        parameters.put("lat",location.getLat());
        parameters.put("lon",location.getLon());
        try {
            json = OkHttp.getOKHttp(URLs.URLCOUNTRYNAME,parameters);
            Log.d("countryLog",json);
            JSONObject jsonObject = new JSONObject(json);
            JSONObject addressObj = jsonObject.getJSONObject("address");
            String country_code = addressObj.getString("country_code");
            getCountryInfo(country_code);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void getCountryInfo(String country_code){
        String json;
        try {
            json = OkHttp.getOKHttp(URLs.URLCOUNTRYINFO+country_code,null);
            if(json != null && json.length() > 0 ){
                JSONObject jsonObject = new JSONObject(json);
                this.country.setAlpha2Code(jsonObject.getString("alpha2Code"));
                this.country.setAlpha3Code(jsonObject.getString("alpha3Code"));
                this.country.setName(jsonObject.getString("name"));
                this.country.setCapital(jsonObject.getString("capital"));
                JSONArray altSpellings = jsonObject.getJSONArray("altSpellings");
                if(altSpellings.length() > 1){
                    this.country.setFullname(altSpellings.getString(1));
                }
                this.country.setRegion(jsonObject.getString("region"));
                this.country.setSubregion(jsonObject.getString("subregion"));
                this.country.setPopulation(jsonObject.getString("population"));
                JSONArray languages = jsonObject.getJSONArray("languages");
                this.country.setLanguages(new JSONObject(languages.getString(0)).getString("name"));
                JSONArray currencies = jsonObject.getJSONArray("currencies");
                this.country.setCurrencies(new JSONObject(currencies.getString(0)).getString("code"));
                this.country.setFlag(jsonObject.getString("flag"));
            }
        } catch (IOException | JSONException e) {
            Log.d("ErrorAPICountry",e.toString());
        }
        send(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        onCreate();
    }
}
