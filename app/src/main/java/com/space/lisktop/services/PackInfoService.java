package com.space.lisktop.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.space.lisktop.R;
import com.space.lisktop.activities.FragRight;
import com.space.lisktop.utility.LisktopDAO;


/**
 * 处理应用安装、卸载操作数据库、更改列表事务
 */
public class PackInfoService extends IntentService {
    public PackInfoService()
    {super("PackInfoService");}

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder builder = new Notification.Builder(this.getApplicationContext())
                    //.setContentIntent(PendingIntent.getActivity(this, 0, new PendingIntent(), 0)) // 设置PendingIntent
                    .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setContentText("处理应用安装/卸载事件") // 设置上下文内容
                    .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                //修改安卓8.1以上系统报错
                String CHANNEL_ID="0";
                NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "zero",NotificationManager.IMPORTANCE_MIN);
                notificationChannel.enableLights(false);//如果使用中的设备支持通知灯，则说明此通知通道是否应显示灯
                notificationChannel.setShowBadge(false);//是否显示角标
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manager.createNotificationChannel(notificationChannel);
                builder.setChannelId(CHANNEL_ID);
            }

            Notification notification = builder.build(); // 获取构建好的Notification
            notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音

            startForeground(1, notification); //这个id不要和应用内的其他同志id一样，不行就写 int.maxValue()        //context.startForeground(SERVICE_ID, builder.getNotification());
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Bundle bundle=intent.getExtras();
        int action=bundle.getInt("action");    //操作类型，0安装、1卸载
        String package_name=bundle.getString("appPackName");

        Log.i("packService","handle intent"+action+package_name);
        LisktopDAO lisktopDAO=new LisktopDAO(getApplicationContext());

        switch (action)
        {
            case 0:
                //获取包信息添加数据库
                PackageManager pm=getApplicationContext().getPackageManager();
                try {
                    PackageInfo packInfo=pm.getPackageInfo(package_name,0);
                    String app_name=(String)packInfo.applicationInfo.loadLabel(pm);
                    Drawable app_icon=packInfo.applicationInfo.loadIcon(pm);
                    Log.i("packService","anzhuang:"+app_name);

                    lisktopDAO.insertInstalledApp(package_name,app_name,app_icon);
                    FragRight.appHandler.sendEmptyMessage(0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case 1:
                //删除数据库记录
                lisktopDAO.deleteUninstalledApp(package_name);
                FragRight.appHandler.sendEmptyMessage(0);
                break;
        }
    }
}
