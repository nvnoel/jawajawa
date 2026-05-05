package com.rockstargames.hal;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import java.util.Calendar;

public class andNotification {
    static final  boolean $assertionsDisabled;
    private static int notificationIDCount;

    static {
        $assertionsDisabled = !andNotification.class.desiredAssertionStatus();
        notificationIDCount = 0;
    }

    public static void AddNotification(String name, String title, String body, int time) {
        Intent i = new Intent("com.rockstargames.hal.Broadcast");
        i.setClass(ActivityWrapper.getActivity(), andAlarmReceiver.class);
        i.putExtra("notificationTitle", title);
        i.putExtra("notificationMsg", body);
        i.putExtra("notificationID", notificationIDCount);
        PendingIntent alarmSender = PendingIntent.getBroadcast(ActivityWrapper.getActivity(), notificationIDCount, i, 0);
        Calendar c = Calendar.getInstance();
        c.add(13, time);
        long firstTime = c.getTimeInMillis();
        AlarmManager am = (AlarmManager) ActivityWrapper.getActivity().getSystemService("alarm");
        am.set(0, firstTime, alarmSender);
        notificationIDCount++;
        if (!$assertionsDisabled && notificationIDCount >= 16) {
            throw new AssertionError();
        }
    }

    public static void CancelAllNotifications() {
        AlarmManager am = (AlarmManager) ActivityWrapper.getActivity().getSystemService("alarm");
        for (int i = 0; i < 16; i++) {
            Intent removeIntent = new Intent("com.rockstargames.hal.Broadcast");
            removeIntent.setClass(ActivityWrapper.getActivity(), andAlarmReceiver.class);
            PendingIntent alarmSender = PendingIntent.getBroadcast(ActivityWrapper.getActivity(), i, removeIntent, 536870912);
            if (alarmSender != null) {
                am.cancel(alarmSender);
            }
        }
        notificationIDCount = 0;
    }
}