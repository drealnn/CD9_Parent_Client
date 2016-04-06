package com.example.daniel.cd9_parent_client.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;


import com.example.daniel.cd9_parent_client.JSONClassFiles.GcmToken;
import com.example.daniel.cd9_parent_client.JSONClassFiles.User;
import com.example.daniel.cd9_parent_client.R;

import com.example.daniel.cd9_parent_client.Utils;
import com.example.daniel.cd9_parent_client.networking.CD9ClientInterface;
import com.example.daniel.cd9_parent_client.networking.ServiceGenerator;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.api.client.auth.oauth2.Credential;

import java.io.IOException;

import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Response;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class GCMRegisterationIntentService extends IntentService {
    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};

    public GCMRegisterationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            // [START get_token]



            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]
            Log.e(TAG, "GCM Registration Token: " + token);

            // TODO: Implement this method to send any registration to your app's servers.
            sendRegistrationToServer(token);

            // Subscribe to topic channels
            subscribeTopics(token);

            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            sharedPreferences.edit().putBoolean(getString(R.string.SENT_GCM_TOKEN_TO_SERVER), true).apply();
            // [END register_for_gcm]
        } catch (Exception e) {
            Log.e(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(getString(R.string.SENT_GCM_TOKEN_TO_SERVER), false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        //Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        //LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.

        //TODO: code is messy, could use refactoring. look at GatherIntentService for better example

        String userId = "null";

        SharedPreferences pref = this.getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        String authToken = pref.getString(getString(R.string.custom_token_preference_key), "null");

        User[] user;
        CD9ClientInterface client = ServiceGenerator.CreateService(CD9ClientInterface.class, Utils.getGlobalString(this, "USERNAME"), Utils.getGlobalString(this, "PASSWORD"));
        Call<User[]> getTeenID = client.getIds();
        try {
            Response response1 = getTeenID.execute();



            if (!response1.isSuccess())
                Log.e(TAG, "Bad response when trying to retrieve teen id");
            else {
                user = (User[]) response1.body();
                Log.e(TAG, user[1].getId());
                userId = user[1].getId();

            }
        }catch (IOException e)
        {
            Log.e(TAG, "IOException when trying to retrieve teen id");
        }


        Call<GcmToken> sendGcmTokenCall = client.sendGcmToken(userId, new GcmToken(token));

        if (!userId.equals("null")) {
            try {
                Response response = sendGcmTokenCall.execute();

                if (!response.isSuccess()) {
                    Log.e(TAG, "Bad Response");
                    Toast.makeText(this, "Bad Request from CD9 Server", Toast.LENGTH_LONG).show();

                }

            } catch (IOException e) {
                Log.e(TAG, "IOException");
                Toast.makeText(this, "Unable to connect to server. Check network status or contact server administrator.", Toast.LENGTH_LONG).show();


            }
        }
        else
            Log.e(TAG, "Can't find user id. GCM registration aborted");

    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]

}
