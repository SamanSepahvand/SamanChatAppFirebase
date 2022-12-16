package com.samansepahvand.samanchapapp.firebase;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.e(TAG, "onMessageReceived: "+remoteMessage.getNotification().getBody() );
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

