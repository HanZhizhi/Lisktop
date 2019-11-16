package com.space.lisktop.bcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class packInfoReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("received",intent.toString());
        String packName,action=intent.getAction();
        if (Intent.ACTION_PACKAGE_ADDED.equals(action))
        {
            packName=intent.getData().getSchemeSpecificPart();
            Log.i("recverinstall","1:"+packName+"2"+action);
            Toast.makeText(context,"anzhuang"+packName,Toast.LENGTH_LONG).show();
        }
        if (Intent.ACTION_PACKAGE_REMOVED.equals(action))
        {
            packName=intent.getData().getSchemeSpecificPart();
            Log.i("recverremove","1:"+packName+"2"+action);
            Toast.makeText(context,"anzhuang"+packName,Toast.LENGTH_LONG).show();
        }
        if ("hahaTest".equals(action))
        {
            Log.i("hahaTest","from packInfoRecver");
        }
    }
}
