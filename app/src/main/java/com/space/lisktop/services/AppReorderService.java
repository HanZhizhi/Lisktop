package com.space.lisktop.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.space.lisktop.LisktopApp;
import com.space.lisktop.MainActivity;
import com.space.lisktop.R;
import com.space.lisktop.activities.FragRight;
import com.space.lisktop.obj.AppInfo;
import com.space.lisktop.utility.AppClickedSorter;
import com.space.lisktop.utility.LisktopDAO;

import java.util.ArrayList;

public class AppReorderService extends IntentService {

    private LisktopDAO lisktopDAO;

    public AppReorderService() {
        super("AppClickService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder builder = new Notification.Builder(this.getApplicationContext())
                    //.setContentIntent(PendingIntent.getActivity(this, 0, new PendingIntent(), 0)) // 设置PendingIntent
                    .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setContentText("") // 设置上下文内容
                    .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

            String CHANNEL_ID="1";
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "zero", NotificationManager.IMPORTANCE_MIN);
            notificationChannel.enableLights(false);//如果使用中的设备支持通知灯，则说明此通知通道是否应显示灯
            notificationChannel.setShowBadge(false);//是否显示角标
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
            builder.setChannelId(CHANNEL_ID);

            Notification notification = builder.build(); // 获取构建好的Notification
            notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音

            startForeground(LisktopApp.FORE_SERVICE_NOTIFICATION_CHANNEL_USG, notification); //这个id不要和应用内的其他通知id一样，不行就写 int.maxValue()        //context.startForeground(SERVICE_ID, builder.getNotification());
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Bundle bd=intent.getExtras();
        String package_name=bd.getString("packName");    //,application_name=bd.getString("appName");

        lisktopDAO=new LisktopDAO(getApplicationContext());
        ArrayList<AppInfo> appList1=lisktopDAO.getUnhiddenApps();

        int clicked_index=lisktopDAO.getRightIndex(package_name)-1;  //用于排序的下标为ArrayList下标，而数据库中从1开始

        if(clicked_index == -2)
        {
            Log.i("service",": return -1");
            return;
        }

        Log.i("service",":"+package_name+clicked_index);
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
