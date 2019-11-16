package com.space.lisktop.bcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class TimeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction())
        {
            case Intent.ACTION_TIME_TICK:
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                boolean is24hFormart = true;
                if (!is24hFormart && hour >= 12) {
                    hour = hour - 12;
                }

                String time = "";
                if (hour >= 10) {
                    time += Integer.toString(hour);
                }
                else {
                    time += "0" + Integer.toString(hour);
                }
                time += ":";

                if (minute >= 10) {
                    time += Integer.toString(minute);
                }
                else {
                    time += "0" + Integer.toString(minute);
                }

                Log.i("tick","time_tick"+time);
        }
    }
}
