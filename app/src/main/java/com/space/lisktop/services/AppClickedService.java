package com.space.lisktop.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;

import com.space.lisktop.MainActivity;

public class AppClickedService extends IntentService {

    public AppClickedService() {
        super("AppClickService");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Bundle b=intent.getExtras();
        String package_name=b.getString("appPackName");

        //TODO: 排序、数据库操作
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        MainActivity.mainHandler.sendEmptyMessage(1);
        Log.i("appclickreceiver",package_name);
    }
}
