package com.final_sem_project.tomhardy.sensorservice;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by TomHardy on 24-02-2018.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private LocalBroadcastManager broadcaster;

    @Override
    public void onCreate() {
        Log.d("FirebaseMessageService", "onCreate");
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

//        Log.d("Notification","title");
        if (remoteMessage.getData().size() > 0) {

            if (remoteMessage.getData().get("message") != null) {

                String title = (remoteMessage.getData().get("title") != null) ? remoteMessage.getData().get("title") : "FCM Test2";
//                Log.d("Notification","data "+title);
                showNotification(title, remoteMessage.getData().get("message"));

            }
        }

        // When you send notification from the firebase console (avoid it for now. Is still very new)
        if (remoteMessage.getNotification() != null) {

            String title = (remoteMessage.getNotification().getTitle() != null) ? remoteMessage.getNotification().getTitle() : "FCM Test2 Console";

//            Log.d("Notification",title);
            showNotification(title, remoteMessage.getNotification().getBody());
        }
//        showNotification(remoteMessage.getData().get("message"));
    }

    private void showNotification(String title, String message) {
        Log.d("Notification :", title + " came  " + message);

        initChannels(this);

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");
        builder.setAutoCancel(true);
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setSmallIcon(R.drawable.instruction_icon);
        builder.setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(0, builder.build());


        Intent intentProfileActivity = new Intent("Notification");
        broadcaster.sendBroadcast(intentProfileActivity);
    }

    public void initChannels(Context context) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("default",
                "Channel name",
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Channel description");
        notificationManager.createNotificationChannel(channel);
    }
}