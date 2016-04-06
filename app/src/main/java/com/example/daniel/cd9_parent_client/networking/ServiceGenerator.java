package com.example.daniel.cd9_parent_client.networking;

import android.util.Base64;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

/**
 * Created by daniel on 1/19/16.
 */
public class ServiceGenerator {
    public static final String BASE_URL = "http://209.208.28.59:8000"; // Change to appropriate address



    // using an OkHttpClient.Builder so that we can build in header info into our client such that every http request utilizes
        // the same information for authentication and the data format we are sending
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();


    // the Gson converter specified here is what converts our java objects to json objects and vice-versa
    private static Retrofit.Builder builder = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create());

    public static <S> S CreateService(Class<S> serviceClass)
    {
        return CreateService(serviceClass, null, null);
    }

    public static <S> S CreateService(Class<S> serviceClass, String username, String password) {

        // serviceClass is our client interface that defines the API endpoints for GET and POST requests

        // We need to encode the username:password in base64 so that the server can processes
        if (username != null && password != null) {
            String credentials = username + ":" + password;
            final String basic =
                    "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Request original = chain.request();

                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Authorization", basic)
                            .header("Accept", "application/json")
                            .method(original.method(), original.body());

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });
        }



        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.interceptors().add(logging);

        OkHttpClient myclient = httpClient.build();
        Retrofit retrofit = builder.client(myclient).build();
        return retrofit.create(serviceClass);

    }
}
