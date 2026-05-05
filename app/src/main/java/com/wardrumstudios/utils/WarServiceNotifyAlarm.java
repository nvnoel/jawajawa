package com.wardrumstudios.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;

public class WarServiceNotifyAlarm extends Service {
    public int icon;
    private NotificationManager mManager;

    @Override // android.app.Service
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
    }

    @Override // android.app.Service
    public void onStart(Intent intent, int startId) {
        Intent intent1;
        super.onStart(intent, startId);
        Bundle extras = intent.getExtras();
        String title = extras.getString("title");
        extras.getString("subject");
        String message = extras.getString("message");
        int icon = extras.getInt("icon");
        int smallicon = extras.getInt("smallicon");
        int notifyId = extras.getInt("notifyId");
        String appClassString = extras.getString("class");
        String key = "Alarm" + notifyId;
        SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(this).edit();
        e.remove(key);
        e.commit();
        Resources res = getApplicationContext().getResources();
        Context applicationContext = getApplicationContext();
        getApplicationContext();
        this.mManager = (NotificationManager) applicationContext.getSystemService("notification");
        try {
            Class appClass = Class.forName(appClassString);
            intent1 = new Intent(getApplicationContext(), appClass);
        } catch (ClassNotFoundException e2) {
            intent1 = new Intent();
        }
        intent1.addFlags(603979776);
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent1, 134217728);
        Notification notification = new Notification.Builder(this).setContentTitle(title).setContentText(message).setSmallIcon(smallicon).setLargeIcon(BitmapFactory.decodeResource(res, icon)).setAutoCancel(true).setDefaults(4).setTicker(message).setContentIntent(pendingNotificationIntent).build();
        this.mManager.notify(notifyId, notification);
        if (WarService.myWarService != null) {
            WarService.myWarService.UpdateBadgeReceiver(true, appClassString);
        }
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
    }
}