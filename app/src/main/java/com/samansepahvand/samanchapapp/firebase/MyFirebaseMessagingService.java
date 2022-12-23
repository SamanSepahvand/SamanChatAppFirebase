package com.samansepahvand.samanchapapp.firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;


import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.samansepahvand.samanchapapp.R;
import com.samansepahvand.samanchapapp.activities.ChatActivity;
import com.samansepahvand.samanchapapp.models.User;
import com.samansepahvand.samanchapapp.utilities.Constants;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

      //  Log.e(TAG, "onMessageReceived: "+remoteMessage.getNotification().getBody() );


        User user=new User();
        user.id=remoteMessage.getData().get(Constants.KEY_USER_ID);
        user.name =remoteMessage.getData().get(Constants.KEY_NAME);
        user.token=remoteMessage.getData().get(Constants.KEY_FCM_TOKEN);

        int notificationId=new Random().nextInt();
        String channelId="chat_message";
        Intent intent=new Intent(this, ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(Constants.KEY_USER,user);
        
        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity
                    (this, 0, intent, PendingIntent.FLAG_MUTABLE);
        }
        else
        {
            pendingIntent = PendingIntent.getActivity
                    (this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        }


        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,channelId);
        builder.setSmallIcon(R.drawable.ic_baseline_notifications_24);
        builder.setContentTitle(user.name);
        builder.setContentText(remoteMessage.getData().get(Constants.KEY_MESSAGE));
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(
                remoteMessage.getData().get(Constants.KEY_MESSAGE)
        ));
        builder.setPriority(Notification.PRIORITY_DEFAULT);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(false);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){

            CharSequence    channelName="Chat Message";
            String channelDescription="This notification channel is used for chat message notification";

            int importance=NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel=new NotificationChannel(channelId,channelName,importance);
            channel.setDescription(channelDescription);
            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationManagerCompat   notificationManagerCompat=NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(notificationId,builder.build());




    }


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("FCM", s);
        getToken(s);
    }

    private static String getToken(String s) {
        return s;
    }



}

