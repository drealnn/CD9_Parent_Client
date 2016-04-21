package com.example.daniel.cd9_parent_client.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import com.example.daniel.cd9_parent_client.NotificationDialogue;
import com.example.daniel.cd9_parent_client.R;
import com.google.android.gms.gcm.GcmListenerService;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "GcmServicePARENT";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("alert");
        String teen = data.getString("name");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        sendNotification(message, teen);
        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message, String teen) {

        // TODO: Store alerts to be read from a separate activity

//        Intent intent = new Intent(this, MainActivity.class);

 //       intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
  //      PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
   //
   //            PendingIntent.FLAG_ONE_SHOT);

        Intent intent = new Intent(this, NotificationDialogue.class);
        intent.putExtra("message", message);
        intent.putExtra("name", teen);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

     /*

        if (message.contains("Warning: Authorization Code Update Needed !"))
        {
            // prompt google login
            intent.putExtra("Alert", "getNewGoogleToken");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else if (message.contains("Warning: Facebook Token Update Needed !"))
        {
            // prompt facebook login
            intent.setClass(this, SetupActivity.class);
            intent.putExtra("Alert", "getNewFacebookToken");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        */

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Alert")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // TODO: fix the id to a counter instead of relying on system time
        notificationManager.notify((int) System.currentTimeMillis() /* ID of notification */, notificationBuilder.build());


    }
}