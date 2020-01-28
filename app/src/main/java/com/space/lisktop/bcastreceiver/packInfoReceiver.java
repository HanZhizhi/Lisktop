package com.space.lisktop.bcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.space.lisktop.activities.FragRight;
import com.space.lisktop.services.AppClickedService;
import com.space.lisktop.services.PackInfoService;

public class packInfoReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("received",intent.toString());
        String action=intent.getAction();
        //TODO:在此处启动IntentService更改数据库，然后handler
        if (Intent.ACTION_PACKAGE_ADDED.equals(action))
        {
            String packName=intent.getData().getSchemeSpecificPart();

            //使用service
            Intent packAddedIntent=new Intent(context, PackInfoService.class);
            Bundle acBundle=new Bundle();
            acBundle.putString("appPackName",packName);       //传入包名称进行后续操作
            acBundle.putInt("action",0);
            packAddedIntent.putExtras(acBundle);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(packAddedIntent);
            }else {
                context.startService(packAddedIntent);
            }

            Log.i("recverinstall","1:"+packName+"2"+action);

            //改在service中操作：FragRight.appHandler.sendEmptyMessage(0);
        }
        if (Intent.ACTION_PACKAGE_REMOVED.equals(action))
        {
            String packName=intent.getData().getSchemeSpecificPart();
            Log.i("recverremove","1:"+packName+"2"+action);
            Toast.makeText(context,"anzhuang"+packName,Toast.LENGTH_LONG).show();

            Intent packRemovedIntent=new Intent(context, PackInfoService.class);
            Bundle acBundle=new Bundle();
            acBundle.putString("appPackName",packName);       //传入包名称进行后续操作
            acBundle.putInt("action",1);
            packRemovedIntent.putExtras(acBundle);
            /*
            Android 8.0 还对特定函数做出了以下变更：

（1）如果针对 Android 8.0 的应用尝试在不允许其创建后台服务的情况下使用 startService() 函数，
     则该函数将引发一个 IllegalStateException。新的 Context.startForegroundService() 函数将启动一个前台服务。

（2）即使应用在后台运行，系统也允许其调用 Context.startForegroundService()。不过，
    应用必须在创建服务后的五秒内调用该服务的 startForeground() 函数。
             */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(packRemovedIntent);
            } else {
                context.startService(packRemovedIntent);
            }

            FragRight.appHandler.sendEmptyMessage(0);
        }
        if ("hahaTest".equals(action))
        {
            Log.i("hahaTest","from packInfoRecver");
        }
    }
}
