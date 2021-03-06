package com.example.nemo1.weather21.view;

import com.example.nemo1.weather21.entity.Country;
import com.example.nemo1.weather21.entity.Current;
import com.example.nemo1.weather21.entity.Location;


public interface SendView {
    public void onViewLocation(Location location);
    public void onViewCurrent(Current current);
    public void getCountryInfo(Country countryInfo);
    public void getError(String error);

}
