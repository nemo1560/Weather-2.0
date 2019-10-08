package com.example.nemo1.weather21.custom;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import com.example.nemo1.weather21.service.JobSchedule;

public class ScheduleUtils {

    public static void ScheduleUtils(Context context) {
        ComponentName componentName = new ComponentName(context, JobSchedule.class);
        JobInfo.Builder job = new JobInfo.Builder(0,componentName);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            job.setMinimumLatency(10*60*1000);
            job.setOverrideDeadline(10*60*1000);
        }else {
            job.setPeriodic(10*60*1000);
        }
        JobScheduler schedule = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            schedule = context.getSystemService(JobScheduler.class);
        }
        schedule.schedule(job.build());

    }
}
