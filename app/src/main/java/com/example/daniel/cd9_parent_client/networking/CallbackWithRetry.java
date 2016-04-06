package com.example.daniel.cd9_parent_client.networking;

import retrofit2.Call;

/**
 * Created by daniel on 1/29/16.
 */
public abstract class CallbackWithRetry <T> implements retrofit2.Callback<T> {
    final Call<T> call;
    public static final int TOTAL_RETRYS = 2;
    public int retry = 0;

    public CallbackWithRetry(Call<T> call)
    {
        this.call = call;
    }

    public void retry()
    {
        if (++retry < TOTAL_RETRYS)
            call.clone().enqueue(this);
    }

}
