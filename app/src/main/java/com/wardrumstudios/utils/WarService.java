package com.wardrumstudios.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import androidx.core.app.NotificationCompat;
import java.util.ArrayList;

public class WarService {
    private static final String TAG = "WarService";
    public static WarService myWarService = null;
    public boolean done;
    public int icon;
    private String intentClass;
    // Object mBuilder;
    NotificationManager mNotifyManager;
    private ArrayList<WarNotification> notifications;
    private boolean runOnce = true;
    public int smallicon;
    public Activity warActivity;

    public native void jniWarService();

    /* JADX INFO: Access modifiers changed from: private */
    public class WarNotification {
        public long epochTime;
        public int icon;
        public String message;
        public int smallicon;
        public String title;

        public WarNotification(int delay, String title, String message, int icon, int smallicon) {
            this.epochTime = System.currentTimeMillis() + (delay * 1000);
            this.title = title;
            this.message = message;
            this.icon = icon;
            this.smallicon = smallicon;
        }
    }

    public WarService(Activity activity) {
        this.warActivity = null;
        if (this.warActivity == null) {
            this.warActivity = activity;
            jniWarService();
            this.intentClass = activity.getClass().getCanonicalName();
            this.notifications = new ArrayList<>();
            this.done = true;
            myWarService = this;
        }
    }

    private Intent CreateIntent(String title, String message) {
        Intent newIntent = new Intent(this.warActivity, WarServiceNotifyReceiver.class);
        newIntent.putExtra("class", this.intentClass);
        newIntent.putExtra("icon", this.icon);
        newIntent.putExtra("smallicon", this.smallicon);
        newIntent.putExtra("title", title);
        newIntent.putExtra("message", message);
        newIntent.putExtra("notifyId", this.notifications.size());
        return newIntent;
    }

    private WarNotification CreateNotification(int delay, String title, String message, int icon, int smallicon) {
        WarNotification newNotification = new WarNotification(delay, title, message, icon, smallicon);
        this.notifications.size();
        this.notifications.add(newNotification);
        return newNotification;
    }

    public void LocalPushNotification(final int delay, String title, String message) {
        final int id = this.notifications.size();
        final Intent intent = CreateIntent(title, message);
        WarNotification notification = CreateNotification(delay, title, message, this.icon, this.smallicon);
        String key = "Alarm" + id;
        String alarmSetting = String.valueOf(notification.epochTime);
        SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(this.warActivity).edit();
        e.putString(key, ((((alarmSetting + "|" + String.valueOf(this.smallicon)) + "|" + String.valueOf(this.icon)) + "|" + title) + "|" + message) + "|" + this.intentClass);
        e.commit();
        this.done = false;
        this.warActivity.runOnUiThread(new Runnable() { // from class: com.wardrumstudios.utils.WarService.1
            @Override // java.lang.Runnable
            public void run() {
                PendingIntent pendingIntent = PendingIntent.getBroadcast(WarService.this.warActivity, id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) WarService.this.warActivity.getSystemService("alarm");
                alarmManager.set(2, SystemClock.elapsedRealtime() + (delay * 1000), pendingIntent);
                WarService.this.done = true;
            }
        });
        while (!this.done) {
            Thread.yield();
        }
    }

    public void LocalPushNotificationCancel() {
        NotificationManager notificationManager = (NotificationManager) this.warActivity.getSystemService("notification");
        notificationManager.cancelAll();
        Intent tempIntent = CreateIntent("", "");
        for (int i = 0; i < 10; i++) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.warActivity, i, tempIntent, 0);
            pendingIntent.cancel();
            AlarmManager alarmManager = (AlarmManager) this.warActivity.getSystemService("alarm");
            alarmManager.cancel(pendingIntent);
            String key = "Alarm" + i;
            SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(this.warActivity).edit();
            e.remove(key);
            e.commit();
        }
        this.notifications.clear();
        UpdateBadgeReceiver(false, this.intentClass);
    }

    public void UpdateBadgeReceiver(boolean state, String appClassString) {
        if (Build.MANUFACTURER.equals("Sony")) {
            try {
                Intent intent = new Intent();
                intent.setAction("com.sonyericsson.home.action.UPDATE_BADGE");
                intent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME", appClassString);
                intent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", "1");
                intent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME", this.warActivity.getPackageName());
                intent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", state);
                this.warActivity.sendBroadcast(intent);
            } catch (Exception e) {
            }
        }
        if (Build.MANUFACTURER.equals("HTC")) {
            try {
                Intent localIntent1 = new Intent("com.htc.launcher.action.UPDATE_SHORTCUT");
                localIntent1.putExtra("packagename", this.warActivity.getPackageName());
                localIntent1.putExtra("count", 1);
                this.warActivity.sendBroadcast(localIntent1);
                Intent localIntent2 = new Intent("com.htc.launcher.action.SET_NOTIFICATION");
                ComponentName localComponentName = new ComponentName(this.warActivity, appClassString);
                localIntent2.putExtra("com.htc.launcher.extra.COMPONENT", localComponentName.flattenToShortString());
                localIntent2.putExtra("com.htc.launcher.extra.COUNT", 1);
                this.warActivity.sendBroadcast(localIntent2);
            } catch (Exception e2) {
            }
        }
        try {
            Uri uri = Uri.parse("content://com.sec.badge/apps");
            Cursor c = this.warActivity.getContentResolver().query(uri, null, null, null, null);
            if (c != null) {
                c.close();
                if (state) {
                    ContentValues cv = new ContentValues();
                    cv.put("package", this.warActivity.getPackageName());
                    cv.put("class", appClassString);
                    cv.put("badgecount", (Integer) 1);
                    this.warActivity.getContentResolver().insert(Uri.parse("content://com.sec.badge/apps"), cv);
                    return;
                }
                ContentValues cv2 = new ContentValues();
                cv2.put("badgecount", (Integer) 0);
                this.warActivity.getContentResolver().update(Uri.parse("content://com.sec.badge/apps"), cv2, "package=?", new String[]{this.warActivity.getPackageName()});
            }
        } catch (Exception e3) {
        }
    }
}