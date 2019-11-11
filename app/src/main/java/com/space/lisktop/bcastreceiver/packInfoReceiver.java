package com.space.lisktop.bcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class packInfoReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String packName=intent.getDataString(),action=intent.getAction();
        if ("android.intent.action.PACKAGE_ADDED".equals(action))
        {
            Log.i("recverinstall","1:"+packName+"2"+action);
            Toast.makeText(context,"anzhuang"+packName,Toast.LENGTH_LONG).show();
        }
        if ("android.intent.action.PACKAGE_REMOVED".equals(action))
        {
            Log.i("recverremove","1:"+packName+"2"+action);
            Toast.makeText(context,"anzhuang"+packName,Toast.LENGTH_LONG).show();
        }
    }
}
