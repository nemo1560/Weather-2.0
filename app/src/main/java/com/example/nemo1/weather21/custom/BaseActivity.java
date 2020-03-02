package com.example.nemo1.weather21.custom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BaseActivity extends FragmentActivity {
    private Context context;
    private final static int REQUEST_CODE = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = BaseActivity.this;
    }

    //Demo
    /*final List<String> reqPermissions = Arrays.asList(Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE);*/

    public void listPermissionRequest(List<String> reqPermissions) {
        final ArrayList<String> permissionsNeeded = getPermissionNeeded(new ArrayList<>(reqPermissions));
        if (!permissionsNeeded.isEmpty()) {
            requestForPermission(permissionsNeeded.toArray(new String[permissionsNeeded.size()]));
        } else {
            //action
        }
    }

    private ArrayList<String> getPermissionNeeded(final ArrayList<String> reqPermissions) {
        final ArrayList<String> permissionNeeded = new ArrayList<>(reqPermissions.size());

        for (String reqPermission : reqPermissions) {
            if (ContextCompat.checkSelfPermission(BaseActivity.this, reqPermission) != PackageManager.PERMISSION_GRANTED) {
                permissionNeeded.add(reqPermission);
            }
        }

        return permissionNeeded;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                final int numOfRequest = grantResults.length;
                boolean isGranted = true;
                for (int i = 0; i < numOfRequest; i++) {
                    if (PackageManager.PERMISSION_GRANTED != grantResults[i]) {
                        isGranted = false;
                        break;
                    }
                }
                //action
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void requestForPermission(final String[] permissions) {
        ActivityCompat.requestPermissions(BaseActivity.this, permissions, REQUEST_CODE);
    }

    public void Alert(String title, String massage) {
        final AlertDialog builder = new AlertDialog.Builder(context).create();
        builder.setTitle(title);
        builder.setMessage(massage);
        builder.setButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                builder.dismiss();
            }
        });
        builder.show();

    }

    public void Confirm(String title, String massage, DialogInterface.OnClickListener ok) {
        final AlertDialog builder = new AlertDialog.Builder(context).create();
        builder.setTitle(title);
        builder.setMessage(massage);
        builder.setButton2("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                builder.dismiss();
            }
        });
        builder.setButton("OK", ok);
        builder.show();
    }

    public SharedPreferences share(){
        SharedPreferences preference = getSharedPreferences("weather",0);
        return preference;
    }

    public boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public static String getCurrentDate(Date date){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(date);
    }

    public static String getCurrentTime(Date date){
        SimpleDateFormat format = new SimpleDateFormat("HHMMss");
        return format.format(date);
    }
}
