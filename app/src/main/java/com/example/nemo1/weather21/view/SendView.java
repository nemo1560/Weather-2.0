package com.example.nemo1.weather21.view;

import com.example.nemo1.weather21.entity.Condition;
import com.example.nemo1.weather21.entity.Country;
import com.example.nemo1.weather21.entity.Current;
import com.example.nemo1.weather21.entity.Location;

import java.io.IOException;

public interface SendView {
    public void onViewLocation(Location location);
    public void onViewCurrent(Current current);
    public void onViewCondition(Condition condition);
    public void getCountryInfo(Country countryInfo);

}
