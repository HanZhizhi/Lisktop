package com.space.lisktop.bcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.util.Log;

import com.space.lisktop.MainActivity;

public class HomePressBReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();
        if(Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)){
            String reason=intent.getStringExtra("reason");
            if (reason!=null){
                switch (reason){
                    case "homekey":
                        Log.i("home receiver","home pressed");
                        Message msg=new Message();
                        msg.what=0;      //0代表点击home
                        MainActivity.mainHandler.sendMessage(msg);         //或sendEmptyMessage(0)
                        break;
                    case "recentapps":
                        Log.i("home receiver","recent pressed");
                        break;
                    case "assist":
                        Log.i("home receiver","assist pressed");
                        break;
                }
            }
        }
    }
}
