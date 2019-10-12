package com.example.nemo1.weather21.model;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

/*
*
* Class get location, use GoogleAPIclient library. implementation 'com.google.android.gms:play-services-location:10.0.1'
*
* */

public class GetLocation implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private Context context;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private String coordinateString;
    private LatLng coordinate;
    private SendLocation sendLocation;
    private static final long UPDATE_INTERVAL = 1000;
    private static final long FASTEST_INTERVAL = 1000;

    public GetLocation(Context context,SendLocation sendLocation) {
        this.context = context;
        this.sendLocation = sendLocation;
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        buildLocationRequest();
        buildGoogleAPIClientRequest();
        googleApiClient.connect();
    }

    //build LocationRequest object
    private void buildLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
    }

    //build GoogleAPIClientRequest object
    private void buildGoogleAPIClientRequest() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected( Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null){
            coordinateString = location.getLatitude()+","+location.getLongitude();
            coordinate = new LatLng(location.getLatitude(),location.getLongitude());
            Log.d("location-log",coordinateString);
            sendLocation.onSendLocation(coordinateString);
            sendLocation.onSendLocationLatlng(coordinate);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(context,"Can't get location current, check GPS device please !",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        String locationString = location.getLatitude()+","+location.getLongitude();
        Log.d("location-log",locationString);
    }
}
