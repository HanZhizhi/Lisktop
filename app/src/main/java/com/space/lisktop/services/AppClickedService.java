package com.space.lisktop.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.space.lisktop.LisktopApp;
import com.space.lisktop.MainActivity;
import com.space.lisktop.activities.FragRight;
import com.space.lisktop.obj.AppInfo;
import com.space.lisktop.utility.AppClickedSorter;
import com.space.lisktop.utility.LisktopDAO;

import java.util.ArrayList;

public class AppClickedService extends IntentService {

    private LisktopDAO lisktopDAO;

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
        Bundle bd=intent.getExtras();
        String package_name=bd.getString("packName"),application_name=bd.getString("appName");

        lisktopDAO=new LisktopDAO(getApplicationContext());
        ArrayList<AppInfo> appList1=lisktopDAO.getAllApps();

        int clicked_index=lisktopDAO.getRightIndex(package_name,application_name)-1;  //用于排序的下标为ArrayList下标，而数据库中从1开始

        Log.i("service",":"+package_name+clicked_index+application_name);
        AppClickedSorter.Sort(LisktopApp.getSortMethod(),appList1,clicked_index);   //排序
        lisktopDAO.reOrderApps(appList1);                                 //写库

        // 防止应用打开前先转到左页
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        MainActivity.mainHandler.sendEmptyMessage(1);    //点击后返回左页
        FragRight.lHandler.sendEmptyMessage(0);        //右页列表刷新
    }
}
