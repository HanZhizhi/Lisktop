package com.space.lisktop.services;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.space.lisktop.LisktopApp;

import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class UsgStatsService extends Service {
    private static final String TAG="stats service";
    private Timer mTimer;

    @Override
    public void onCreate() {
        super.onCreate();
        mTimer = new Timer();
        Log.i(TAG,"onCreate00");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG,"onBind");
        return null;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG,"onStart");
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(LisktopApp.isUsageStasticGranted()){
                    String recentPackname=getTopApp();
                    Log.e(TAG,"app:"+recentPackname);
                    Intent reorder=new Intent(UsgStatsService.this, AppReorderService.class);
                    Bundle packBundle=new Bundle();
                    packBundle.putString("packName",recentPackname);
                    reorder.putExtras(packBundle);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(reorder);
                    } else {
                        startService(reorder);
                    }
                }
                else
                    Log.e(TAG,"Not granted permmision !");
            }
        };

        mTimer.schedule(task, 1000, 3000);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestory");
    }

    private String getTopApp() {
        String ret=null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            UsageStatsManager mUsageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);//usagestats
            long time = System.currentTimeMillis();
            List<UsageStats> usageStatsList = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, time - 1000, time);

            if (usageStatsList != null && !usageStatsList.isEmpty()) {
                SortedMap<Long, UsageStats> usageStatsMap = new TreeMap<>();
                for (UsageStats usageStats : usageStatsList) {
                    Log.i(TAG, "getTopApp: package"+getPackageName());
                    if(getPackageName().equals(usageStats.getPackageName())) continue;
                    usageStatsMap.put(usageStats.getLastTimeUsed(), usageStats);
                }

                if (!usageStatsMap.isEmpty()) {
                    String topPackageName = usageStatsMap.get(usageStatsMap.lastKey()).getPackageName();
                    ret=topPackageName;

                    /*//模拟home键点击
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    startActivity(intent);*/
                }
            }
        }
        else {
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            //4.获取正在开启应用的任务栈
            List<ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(1);
            ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);
            //5.获取栈顶的activity,然后在获取此activity所在应用的包名
            //String packagename = runningTaskInfo.baseActivity.getPackageName();
            Log.i(TAG,"system < lollipop"+runningTaskInfo.toString());
            ret=null;
        }
        return ret;
    }
}
