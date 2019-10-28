package com.example.nemo1.weather21.custom;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;

import com.example.nemo1.weather21.service.JobSchedule;

public class ScheduleUtils {

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void ScheduleUtils(Context context) {
        ComponentName componentName = new ComponentName(context, JobSchedule.class);
        JobInfo.Builder job = new JobInfo.Builder(0,componentName);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            job.setMinimumLatency(2*60*60*1000);
            job.setOverrideDeadline(2*60*60*1000);
        }else {
            job.setPeriodic(2*60*60*1000);
        }
        JobScheduler schedule = null;
        schedule = context.getSystemService(JobScheduler.class);
        schedule.schedule(job.build());

    }
}
