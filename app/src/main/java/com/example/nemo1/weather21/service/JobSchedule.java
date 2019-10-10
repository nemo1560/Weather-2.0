package com.example.nemo1.weather21.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.RequiresApi;

import com.example.nemo1.weather21.custom.ScheduleUtils;

public class JobSchedule extends JobService {
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onStartJob(JobParameters params) {
        Intent intent = new Intent(getApplicationContext(), NotiService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getApplicationContext().startForegroundService(intent);
        }else {
            getApplicationContext().startService(intent);
        }
        ScheduleUtils.ScheduleUtils(getApplicationContext());
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
