package com.devsoftzz.doctorassist.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String s = intent.getStringExtra("medicine");
        int _id = intent.getIntExtra("id",0);
        NotificationHelper notificationHelper = new NotificationHelper(context,s);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification();
        notificationHelper.getManager().notify(_id, nb.build());
    }
}
