package com.example.android_application.firebase;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessageService extends FirebaseMessagingService {
    @Override
    // Receive Token from cloud messaging (Firebase)
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        // Search in logcat "FCM" to see the token
        //Log.d("FCM", "Token: " + token);
    }

    @Override
    // Receive Message from cloud messaging service (Firebase)
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // Search in logcat "FCM" to see the receive message notification
        //Log.d("FCM", "Message: " + remoteMessage.getNotification().getBody());
    }
}
