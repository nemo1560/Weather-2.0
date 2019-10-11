package com.example.nemo1.weather21.entity;

import org.json.JSONArray;

import java.io.Serializable;

public class Current implements Serializable {

    private String weather_icons;

    private String weather_descriptions;

    private String temperature;

    private String wind_speed;

    private String wind_dir;

    private String wind_degree;

    private String is_day;

    private String pressure;

    private String humidity;

    private String uv_index;

    private String cloudcover;

    private String feelslike;

    private String visibility;

    public Current() {

    }

    public String getWeather_icons() {
        return weather_icons;
    }

    public String getWeather_descriptions() {
        return weather_descriptions;
    }

    public void setWeather_descriptions(String weather_descriptions) {
        this.weather_descriptions = weather_descriptions;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getWind_speed() {
        return wind_speed;
    }

    public void setWind_speed(String wind_speed) {
        this.wind_speed = wind_speed;
    }

    public String getWind_dir() {
        return wind_dir;
    }

    public void setWind_dir(String wind_dir) {
        this.wind_dir = wind_dir;
    }

    public String getWind_degree() {
        return wind_degree;
    }

    public void setWind_degree(String wind_degree) {
        this.wind_degree = wind_degree;
    }

    public String getIs_day() {
        return is_day;
    }

    public void setIs_day(String is_day) {
        this.is_day = is_day;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getUv_index() {
        return uv_index;
    }

    public void setUv_index(String uv_index) {
        this.uv_index = uv_index;
    }

    public String getCloudcover() {
        return cloudcover;
    }

    public void setCloudcover(String cloudcover) {
        this.cloudcover = cloudcover;
    }

    public String getFeelslike() {
        return feelslike;
    }

    public void setFeelslike(String feelslike) {
        this.feelslike = feelslike;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public void setWeather_icons(String weather_icons) {
        this.weather_icons = weather_icons;
    }
}
