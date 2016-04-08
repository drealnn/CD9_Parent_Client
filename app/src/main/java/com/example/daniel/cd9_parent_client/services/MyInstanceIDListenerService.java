package com.example.daniel.cd9_parent_client.services;

import android.content.Intent;

import com.example.daniel.cd9_parent_client.LoginActivity;
import com.example.daniel.cd9_parent_client.Utils;
import com.google.android.gms.iid.InstanceIDListenerService;


public class MyInstanceIDListenerService extends InstanceIDListenerService {

    private static final String TAG = "MyInstanceIDLS";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        Utils.setGlobalBoolean(this, "HAS_REGISTERED", false);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);


    }
    // [END refresh_token]


}
