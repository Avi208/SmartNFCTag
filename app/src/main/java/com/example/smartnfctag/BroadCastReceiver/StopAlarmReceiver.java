package com.example.smartnfctag.BroadCastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

import com.example.smartnfctag.SubFunctionality.AlarmReceiver;

public class StopAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Stop the ringtone
        Log.d("TestingLogs","onReceive Received Intent");

        AlarmReceiver alarmReceiver = new AlarmReceiver();
        if (alarmReceiver.ringtone != null && alarmReceiver.ringtone.isPlaying()) {
            Log.d("TestingLogs","Ringtone is playing stop");
            alarmReceiver.ringtone.stop();
        }

        // Cancel the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(101);
    }
}