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
        Bundle b=intent.getExtras();
        String package_name=b.getString("appPackName");
        int pos=b.getInt("click_pos");

        lisktopDAO=new LisktopDAO(getApplicationContext());
        ArrayList<AppInfo> appList1=lisktopDAO.getAllApps();

        String packageName_veriyf=appList1.get(pos).getPackageName();
        if (packageName_veriyf.equals(package_name))                          //验证
        {
            AppClickedSorter.Sort(LisktopApp.getSortMethod(),appList1,pos);   //排序
            lisktopDAO.reOrderApps(appList1);                                 //写库
        }

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
