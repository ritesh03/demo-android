package com.maktoday.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.maktoday.R;
import com.maktoday.model.ProfilePicURL;
import com.maktoday.utils.Constants;
import com.maktoday.utils.Log;
import com.maktoday.utils.Prefs;
import com.maktoday.views.main.Main2Activity;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import paytabs.project.MyApplicationContext;

/**
 * Created by cbl81 on 1/12/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sendNotification(remoteMessage);
    }


    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        Log.e(TAG, "sendRegistrationToServer: " + token);
    }

    private void storeRegIdInPref(String token) {
        Prefs.with(MyApplicationContext.getAppContext()).save(Constants.DEVICE_TOKEN, token);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);
        Log.e(TAG, "Token: " + token);
        // Saving reg id to shared preferences
        storeRegIdInPref(token);

        // sending reg id to your server
        sendRegistrationToServer(token);

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent("registrationComplete");
        registrationComplete.putExtra("token", token);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //  sendRegistrationToServer(token);
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(RemoteMessage remoteMessage) {
        Log.e("noti data", remoteMessage.getData().toString());
        String userId = Prefs.with(MyFirebaseMessagingService.this).getString(Constants.USER_ID, null);
      if (userId != null && remoteMessage.getData().get(Constants.FCM_TYPE).equals("MESSAGE") && userId.equals(remoteMessage.getData().get(Constants.RECEIVER_ID))) {
            return;
        }
        ProfilePicURL profilePicURL = new Gson().fromJson(remoteMessage.getData().get(Constants.MAID_PIC), ProfilePicURL.class);
        Intent intent = new Intent(this, Main2Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constants.Notification_DATA, true);
        intent.putExtra(Constants.BODY, remoteMessage.getData().get(Constants.BODY));
        intent.putExtra(Constants.FCM_TYPE, remoteMessage.getData().get(Constants.FCM_TYPE));
        android.util.Log.d(TAG, "sendNotification: "+remoteMessage.getData().get(Constants.FCM_TYPE));
        intent.putExtra(Constants.MAID_ID, remoteMessage.getData().get(Constants.MAID_ID));
        intent.putExtra(Constants.REQ_ID, remoteMessage.getData().get(Constants.REQ_ID));
        intent.putExtra(Constants.MAID_NAME, remoteMessage.getData().get(Constants.MAID_NAME));
        if(remoteMessage.getData().containsKey("link")){
            intent.putExtra(Constants.NOTI_LINK,remoteMessage.getData().get("link"));
        }
        if (profilePicURL != null) {
            intent.putExtra(Constants.MAID_PIC, profilePicURL.getOriginal());
        }
        intent.putExtra(Constants.RECEIVER_ID, remoteMessage.getData().get("senderId"));
        intent.putExtra(Constants.SERVICE_ID, remoteMessage.getData().get(Constants.SERVICE_ID));
        intent.putExtra(Constants.FULL_NAME, remoteMessage.getData().get(Constants.FULL_NAME));
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent,
//                PendingIntent.FLAG_ONE_SHOT);
        PendingIntent pendingIntent;
        android.util.Log.d(TAG, "sendNotification: code number:--- "+ String.valueOf(Build.VERSION_CODES.S));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this,
                    (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        }else {
            pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent,
                    PendingIntent.FLAG_ONE_SHOT);
        }
//
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String channelId = "Default";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,channelId)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher))
                .setContentTitle(remoteMessage.getData().get(Constants.TITLE))
                .setContentText(remoteMessage.getData().get(Constants.BODY))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.group);
        } else {
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);

        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
    }



}
