package com.example.nemo1.weather21.model;

import android.Manifest;
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

/*
*
* Class get location, use GoogleAPIclient library. implementation 'com.google.android.gms:play-services-location:10.0.1'
*
* */

public class GetLocation implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private Context context;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location location;
    private static final long UPDATE_INTERVAL = 1000;
    private static final long FASTEST_INTERVAL = 1000;
    private SendLocation sendLocation;

    public GetLocation(Context context, SendLocation sendLocation) {
        this.context = context;
        this.sendLocation = sendLocation;
        buildLocationRequest();
        buildGoogleAPIClientRequest();
        googleApiClient.connect();
        getGPS();
    }

    //build LocationRequest object
    public void buildLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
    }

    //build GoogleAPIClientRequest object
    public void buildGoogleAPIClientRequest() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onConnected( Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//             TODO: Consider calling
//                ActivityCompat#requestPermissions
//             here to request the missing permissions, and then overriding
//               public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                                      int[] grantResults)
//             to handle the case where the user grants the permission. See the documentation
//             for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location2 = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location2 != null){
            location = location2;
            String locationString = location.getLatitude()+","+location.getLongitude();
            Log.d("location-log",locationString);
            sendLocation.onSendLocation(locationString);
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
        this.location = location;
        String locationString = location.getLatitude()+","+location.getLongitude();
        Log.d("location-log",locationString);
        sendLocation.onSendLocation(locationString);
    }

    public void getGPS(){
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

    }
}
