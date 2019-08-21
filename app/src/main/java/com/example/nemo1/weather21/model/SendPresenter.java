package com.example.nemo1.weather21.model;

import android.graphics.Bitmap;

import com.example.nemo1.weather21.entity.Condition;
import com.example.nemo1.weather21.entity.Country;
import com.example.nemo1.weather21.entity.Current;
import com.example.nemo1.weather21.entity.Location;

import java.io.IOException;

public interface SendPresenter {
    public void getLocationsuccessfully(Location location);
    public void getCurrentsuccessfully(Current current);
    public void getConditionsuccessfully(Condition condition);
    public void getCountryInfo(Country countryInfo);
}
