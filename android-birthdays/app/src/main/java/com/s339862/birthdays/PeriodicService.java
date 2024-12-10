package com.s339862.birthdays;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import androidx.preference.PreferenceManager;

import java.util.Calendar;

// Service for setting up the periodic alarm for sending birthday SMS
public class PeriodicService extends Service {

    // Required method in Service class, but not used in this service
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Runs when the service is started
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The alarm manager is used to schedule the alarm
        // AlarmManager allows us to wake up the device at a specific time
        // and run a specific task, in this case sending birthday SMS
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // The broadcast receiver intent is used to start the SmsBroadcastReceiver
        // This is necessary in order to receive a broadcast when the alarm goes off
        // When the broadcast is received, the SmsBroadcastReceiver will start the SendSmsService
        Intent broadcastReceiverIntent = new Intent(this, SmsBroadcastReceiver.class);

        // The pending intent is used to start the broadcast receiver when the alarm goes off
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, broadcastReceiverIntent, PendingIntent.FLAG_IMMUTABLE);

        // Get the time for the alarm from the shared preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String time = sharedPreferences.getString("sms_time", "08:00");

        // Set the alarm to go off at the specified time every day
        Calendar calendar = Calendar.getInstance();
        // Get the hour and minute from the time string
        int hour = Integer.parseInt(time.split(":")[0]);
        int minute = Integer.parseInt(time.split(":")[1]);
        // Set the calendar to the specified time
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        // Set the alarm to go off at the specified time every day
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        Log.d("PeriodicService", "Alarm set for " + time);
        // Return START_STICKY to make sure the service is restarted if it's killed by the system
        return START_STICKY;
    }

}
