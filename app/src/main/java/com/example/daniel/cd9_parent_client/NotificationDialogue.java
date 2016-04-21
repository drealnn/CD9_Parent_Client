package com.example.daniel.cd9_parent_client;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.daniel.cd9_parent_client.DashBoardActivity;

/**
 * Created by daniel on 4/18/16.
 */
public class NotificationDialogue  extends Activity {



    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Log.e("NOT", "entering notification activity");

        // Get the alarm ID from the intent extra data
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            String message = extras.getString("message", " ");
            String title = extras.getString("name", " ");

            new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("DashBoard", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(NotificationDialogue.this, DashBoardActivity.class);
                            startActivity(i);
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                            finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_dialer)
                    .show();

        }
        else
            Log.e("NOT", "NOT WORKING");

    }
}