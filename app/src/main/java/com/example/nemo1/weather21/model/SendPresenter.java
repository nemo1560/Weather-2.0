package com.example.nemo1.weather21.model;

import com.example.nemo1.weather21.entity.Condition;
import com.example.nemo1.weather21.entity.Current;
import com.example.nemo1.weather21.entity.Location;

public interface SendPresenter {
    public void getLocationsuccessfully(Location location);
    public void getCurrentsuccessfully(Current current);
    public void getConditionsuccessfully(Condition condition);
}
