package com.example.daniel.cd9_parent_client;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
//import android.widget.GridLayout;
//import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.daniel.cd9_parent_client.JSONClassFiles.GcmToken;
import com.example.daniel.cd9_parent_client.JSONClassFiles.User;
import com.example.daniel.cd9_parent_client.networking.CD9ClientInterface;
import com.example.daniel.cd9_parent_client.networking.ServiceGenerator;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {


    TextView tv;


    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent i = new Intent();
        i.setClass(getApplicationContext(), DashBoardActivity.class);

        /**
         * If the parent has already registered, close the activity and start the dashboard
         * Could also probably programmatically change the manifest for launch activity on successful sign
         *  in
         */
        // Probably should use enums/strings.xml for keys...but lazy
        if (Utils.getGlobalBoolean(this, "HAS_REGISTERED"))
        {
            startActivity(i);
            finish();
        }




        // Create alert for parent to explain initial sign up

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);


        builder1.setTitle("Welcome to the parent CD9 application!");

        LayoutInflater inflater = getLayoutInflater();
        builder1.setView(inflater.inflate(R.layout.alert_layout, null));
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });


        AlertDialog alert11 = builder1.create();





        // Set up the login form.
        mUsernameView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        // display alert
        alert11.show();
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mUsernameView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }/* else if (!isEmailValid(email)) {
            mUsernameView.setError(getString(R.string.error_invalid_email));
            focusView = mUsernameView;
            cancel = true;
        }*/

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(username, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mUsernameView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */

    enum ErrorMsg {CANT_REGISTER, NO_ID, CANT_CONNECT, BAD_RESPONSE, NONE};

    public class UserLoginTask extends AsyncTask<Void, Void, ErrorMsg> {

        private final String mUsername;
        private final String mPassword;
        private final String TAG = "UserLoginTask";

        // java bullshit won't let enums inside nested classes
        //enum errorMsg {HELLO};

        UserLoginTask(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        protected void onProgressUpdate(Void... progress) {

        }


        @Override
        protected ErrorMsg doInBackground(Void... params) {

            /**
             * Get registration token from google's GCM service
             */

            InstanceID instanceID = InstanceID.getInstance(LoginActivity.this);
            String token = null;
            try {
                token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            } catch (IOException e) {
                e.printStackTrace();
                return ErrorMsg.CANT_REGISTER;
            }


            Log.e(TAG, "GCM Registration Token: " + token);

            /**
             * Get Parent's id from our server using the supplied credentials
             */

            String userId = "null";
            User[] user;
            CD9ClientInterface client = ServiceGenerator.CreateService(CD9ClientInterface.class, mUsername, mPassword);
            Call<User[]> getParentID = client.getIds();

            try {
                Response response1 = getParentID.execute();

                if (!response1.isSuccess()) {
                    Log.e(TAG, "Bad response when trying to retrieve parent id");
                    return ErrorMsg.NO_ID;
                }
                else {
                    user = (User[]) response1.body();
                    Log.e(TAG, user[0].getId());
                    userId = user[0].getId();

                }

            }catch (IOException e)
            {
                Log.e(TAG, "IOException when trying to retrieve parent id");
                return ErrorMsg.CANT_CONNECT;
            }


            /**
             * Send the GCM token to the server
             */

            Call<GcmToken> sendGcmTokenCall = client.sendGcmToken(userId, new GcmToken(token));


            try {
                Response response = sendGcmTokenCall.execute();

                if (!response.isSuccess()) {
                    Log.e(TAG, "Bad Response when sending gcm token");
                    return ErrorMsg.BAD_RESPONSE;
                }

            } catch (IOException e) {
                Log.e(TAG, "IOException when sending gcm token");
                return ErrorMsg.CANT_CONNECT;

            }

            return ErrorMsg.NONE;
        }

        @Override
        protected void onPostExecute(final ErrorMsg msg) {
            mAuthTask = null;
            showProgress(false);

            switch (msg)
            {
                case NONE:
                    Utils.setGlobalBoolean(LoginActivity.this, "HAS_REGISTERED", true);

                    // TODO: A dialogue popup might be more suitable here
                    Toast.makeText(getApplicationContext(), "Success! You will now receive alerts in the notifcation menu.", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                case CANT_CONNECT:
                    Toast.makeText(getApplicationContext(), "Unable to connect to server. Check network status or contact server administrator.", Toast.LENGTH_LONG).show();
                    break;
                case BAD_RESPONSE:
                    Toast.makeText(getApplicationContext(), "Bad Response from CD9 Server", Toast.LENGTH_LONG).show();
                    break;
                case NO_ID:
                    Toast.makeText(getApplicationContext(), "Unable to lookup parent", Toast.LENGTH_SHORT).show();
                    break;
                case CANT_REGISTER:
                    Toast.makeText(getApplicationContext(), "Failed to register with cloud messaging service.", Toast.LENGTH_SHORT).show();
                    break;


            }


            mPasswordView.setError(getString(R.string.error_incorrect_password));
            mPasswordView.requestFocus();


        }



        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

