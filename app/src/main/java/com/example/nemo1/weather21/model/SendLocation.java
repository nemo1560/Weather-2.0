package com.example.nemo1.weather21.model;

import com.google.android.gms.maps.model.LatLng;

public interface SendLocation {
    public void onSendLocation(String location);
    public void onSendLocationLatlng (LatLng latLng);
}
