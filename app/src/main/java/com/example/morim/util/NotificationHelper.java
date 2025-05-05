package com.example.morim.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import com.example.morim.MorimApp;
import com.example.morim.R;

import java.util.Random;

public class NotificationHelper {
    public static void sendNotification(Context context, String message) {
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        manager.notify(new Random().nextInt(100),
                new Notification.Builder(context, MorimApp.NOTIFICATION_CHANNEL)
                        .setSmallIcon(R.drawable.icon)
                        .setContentText(message)
                        .build()
        );
    }
}
