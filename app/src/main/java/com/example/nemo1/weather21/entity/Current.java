package com.example.nemo1.weather21.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONArray;

import java.io.Serializable;
@Entity(tableName = "CurrentInfo")
public class Current implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name =  "_id")
    private Long _id;

    @ColumnInfo(name =  "weather_icons")
    private String weather_icons;

    @ColumnInfo(name =  "weather_descriptions")
    private String weather_descriptions;

    @ColumnInfo(name =  "temperature")
    private String temperature;

    @ColumnInfo(name =  "wind_speed")
    private String wind_speed;

    @ColumnInfo(name =  "wind_dir")
    private String wind_dir;

    @ColumnInfo(name =  "wind_degree")
    private String wind_degree;

    @ColumnInfo(name =  "is_day")
    private String is_day;

    @ColumnInfo(name =  "pressure")
    private String pressure;

    @ColumnInfo(name =  "humidity")
    private String humidity;

    @ColumnInfo(name =  "uv_index")
    private String uv_index;

    @ColumnInfo(name =  "cloudcover")
    private String cloudcover;

    @ColumnInfo(name =  "feelslike")
    private String feelslike;

    @ColumnInfo(name =  "visibility")
    private String visibility;

    @ColumnInfo(name = "time_data")
    private String time_data;

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

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getTime_data() {
        return time_data;
    }

    public void setTime_data(String time_data) {
        this.time_data = time_data;
    }
}
