package com.wardrumstudios.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WarBootReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent arg1) {
        context.startService(new Intent(context, WarBootNotify.class));
    }
}