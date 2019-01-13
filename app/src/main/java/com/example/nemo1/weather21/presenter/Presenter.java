package com.example.nemo1.weather21.presenter;

import android.content.Context;
import android.widget.ProgressBar;

import com.example.nemo1.weather21.entity.Condition;
import com.example.nemo1.weather21.entity.Current;
import com.example.nemo1.weather21.entity.Location;
import com.example.nemo1.weather21.model.API;
import com.example.nemo1.weather21.model.GetLocation;
import com.example.nemo1.weather21.model.SendLocation;
import com.example.nemo1.weather21.model.SendPresenter;
import com.example.nemo1.weather21.view.SendView;


public class Presenter implements SendPresenter,SendLocation {
    private SendView sendView;
    private ProgressBar progressBar;
    private API api;
    private GetLocation getLocation;
    private Context context;

    public Presenter(SendView sendView, ProgressBar progressBar, Context context) {
        this.sendView = sendView;
        this.progressBar = progressBar;
        this.context = context;
    }

    public void process() {
        getLocation = new GetLocation(context,this);
    }

    @Override
    public void getLocationsuccessfully(Location location) {
        sendView.onViewLocation(location);
    }

    @Override
    public void getCurrentsuccessfully(Current current) {
        sendView.onViewCurrent(current);
    }

    @Override
    public void getConditionsuccessfully(Condition condition) {
        sendView.onViewCondition(condition);
    }

    @Override
    public void onSendLocation(String location) {
        api = new API(this,progressBar,context);
        api.LoadAPI(location);
    }
}
