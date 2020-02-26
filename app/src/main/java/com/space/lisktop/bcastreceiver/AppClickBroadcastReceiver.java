package com.space.lisktop.bcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.space.lisktop.services.AppReorderService;

public class AppClickBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("APP_CLICK_ACTION".equals(intent.getAction())){
            String packageName=intent.getStringExtra("package_name");

            //启动IntentService完成排序、更改数据库、更新列表等操作
            Intent appClickServiceIntent=new Intent(context, AppReorderService.class);
            Bundle acBundle=new Bundle();
            acBundle.putString("appPackName",packageName);       //传入包名称进行后续操作
            appClickServiceIntent.putExtras(acBundle);
            context.startService(appClickServiceIntent);

            Log.i("appclick cast received",packageName+"  and started service");
        }
    }
}
