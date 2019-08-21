package com.example.nemo1.weather21.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.nemo1.weather21.entity.Condition;
import com.example.nemo1.weather21.entity.Country;
import com.example.nemo1.weather21.entity.Current;
import com.example.nemo1.weather21.entity.Location;
import com.example.nemo1.weather21.model.Intents;
import com.example.nemo1.weather21.model.OkHttp;
import com.example.nemo1.weather21.model.SharedPreference;
import com.example.nemo1.weather21.model.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WeatherService extends IntentService {
    private String json;
    private Location location;
    private Current current;
    private Condition condition;
    private Country country;


    public WeatherService() {
        super("WeatherService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        getWeatherData(intent);
    }

    private void send(){
        Intent intent = new Intent(Intents.TEMP);
        intent.putExtra("location",location);
        intent.putExtra("current",current);
        intent.putExtra("condition",condition);
        intent.putExtra("country",country);
        sendBroadcast(intent);
    }

    protected void getWeatherData(Intent intent) {
        location = new Location();
        current = new Current();
        condition = new Condition();
        String coordinates = new SharedPreference(getApplicationContext()).init().getString("location","0");
        String key = "a9919d781737410a90e72432180311";
        Map<String, String> paramaters = new HashMap<String, String>();
        paramaters.put("key",key);
        paramaters.put("q",coordinates);
        try {
            json = OkHttp.getOKHttp(URLs.URLWEATHER,paramaters);
            if(json != null && json.length() > 0){
                JSONObject jsonObject = new JSONObject(json);

                JSONObject locationObj = jsonObject.getJSONObject("location");
                location.setName(locationObj.getString("name"));
                location.setRegion(locationObj.getString("region"));
                location.setCountry(locationObj.getString("country"));
                location.setLat(locationObj.getString("lat"));
                location.setLon(locationObj.getString("lon"));
                location.setTz_id(locationObj.getString("tz_id"));
                location.setLocaltime_epoch(locationObj.getString("localtime_epoch"));
                location.setLocaltime(locationObj.getString("localtime"));

                JSONObject currentObj = jsonObject.getJSONObject("current");
                current.setLast_updated_epoch(currentObj.getString("last_updated_epoch"));
                current.setLast_updated(currentObj.getString("last_updated"));
                current.setTemp_c(currentObj.getString("temp_c"));
                current.setTemp_f(currentObj.getString("temp_f"));
                current.setIs_day(currentObj.getString("is_day"));
                current.setWind_mph(currentObj.getString("wind_mph"));
                current.setWind_kph(currentObj.getString("wind_kph"));
                current.setWind_degree(currentObj.getString("wind_degree"));
                current.setWind_dir(currentObj.getString("wind_dir"));
                current.setPressure_mb(currentObj.getString("pressure_mb"));
                current.setPressure_in(currentObj.getString("pressure_in"));
                current.setPrecip_mm(currentObj.getString("precip_mm"));
                current.setPrecip_in(currentObj.getString("precip_in"));
                current.setHumidity(currentObj.getString("humidity"));
                current.setCloud(currentObj.getString("cloud"));
                current.setFeelslike_c(currentObj.getString("feelslike_c"));
                current.setFeelslike_f(currentObj.getString("feelslike_f"));
                current.setVis_km(currentObj.getString("vis_km"));
                current.setVis_miles(currentObj.getString("vis_miles"));
                current.setUv(currentObj.getString("uv"));

                JSONObject conditionObj = currentObj.getJSONObject("condition");
                condition.setText(conditionObj.getString("text"));
                condition.setIcon(conditionObj.getString("icon"));
                condition.setCode(conditionObj.getString("code"));

                getCountryData();
        }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
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
            String country = addressObj.getString("country");
            getCountryInfo(country);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void getCountryInfo(String country){
        String json;
        try {
            json = OkHttp.getOKHttp(URLs.URLCOUNTRYINFO+country,null);
            if(json != null && json.length() > 0 ){
                JSONArray jsonArray = new JSONArray(json);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
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
                this.country.setCurrencies(new JSONObject(currencies.getString(0)).getString("name"));
                this.country.setFlag(jsonObject.getString("flag"));
            }
        } catch (IOException | JSONException e) {
            Log.d("ErrorAPICountry",e.toString());
        }
        send();
    }
}
