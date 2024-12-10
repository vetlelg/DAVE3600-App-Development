package com.s339862.birthdays;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

// Broadcast receiver that listens for the SEND_BIRTHDAY_SMS broadcast
public class SmsBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("SendSMSBroadcastReceiver", "Received broadcast");
        Intent serviceIntent = new Intent(context, SendSmsService.class);
        context.startService(serviceIntent);
    }
}
