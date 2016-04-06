package com.example.daniel.cd9_parent_client;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by daniel on 4/4/16.
 */
public class Utils {

    static Context ctx;
    static SharedPreferences sharedPref;

    public static void setContext(Context context)
    {
        ctx = context;
        sharedPref = ctx.getSharedPreferences(ctx.getString(R.string.preference_file_key), ctx.MODE_PRIVATE);
    }


    public static boolean getGlobalBoolean(Context context, String key)  {


            setContext(context);

        return sharedPref.getBoolean(key, false);

    }

    public static String getGlobalString(Context context, String key)  {


            setContext(context);

        return sharedPref.getString(key, "null");
    }

    public static void setGlobalBoolean(Context context, String key, Boolean value)
    {
        setContext(context);
        sharedPref.edit().putBoolean(key, value).apply();
    }

    public static void setGlobalString(Context context, String key, String value)
    {
        setContext(context);
        sharedPref.edit().putString(key, value).apply();
    }
}
