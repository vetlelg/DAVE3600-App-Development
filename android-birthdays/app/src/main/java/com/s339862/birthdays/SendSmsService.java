package com.s339862.birthdays;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class SendSmsService extends Service {

    // Database instance to access the database in the entire class
    AppDatabase database;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Called when the service is created
    @Override
    public void onCreate() {
        super.onCreate();
        // Get the database instance
        database = DatabaseClient.getInstance(this).getAppDatabase();
        // Create the notification channel for sending notifications
        createNotificationChannel();
    }

    // Called each time the service is started to send birthday SMS
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        checkBirthdaysAndSendSms();
        // Return START_STICKY to make sure the service is restarted if it's killed by the system
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void createNotificationChannel() {
        Log.d("SendService", "Creating notification channel");
        NotificationChannel channel = new NotificationChannel("birthday_channel", "Birthday SMS", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    // Send an SMS to the specified phone number with the specified message
    private void sendSms(String phone, String message) {
        Log.d("SendService", "Sending SMS to " + phone);
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phone, null, message, null, null);
    }

    // Show a notification with the specified name
    private void showNotification(String name) {
        Log.d("SendService", "Sending notification");
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "birthday_channel")
                .setSmallIcon(R.drawable.ic_birthday)
                .setContentTitle("Birthday SMS sent")
                .setContentText("Birthday SMS sent to " + name)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(1, builder.build());
    }

    // Format the birthday to "dd/MM"
    private String formatBirthday(String birthday) {
        String[] parts = birthday.split("/");
        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        return String.format(Locale.getDefault(), "%02d/%02d", day, month);
    }

    private void checkBirthdaysAndSendSms() {
        Log.d("SendService", "Checking birthdays");
        // Get the SMS message from the shared preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String message = preferences.getString("sms_message", "Happy birthday!");
        // Get the current date in the format "dd/MM"
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM"));

        // Execute the database operation on a separate thread
        // Check if any friends have a birthday today
        // If a friend has a birthday today, send an SMS and show a notification
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Friend> friends = database.friendDao().getAll();
            for (Friend friend : friends) {
                String birthday = formatBirthday(friend.getBirthday());
                if (birthday.equals(today)) {
                    sendSms(friend.getPhone(), message);
                    showNotification(friend.getName());
                }
            }
        });

    }
}
