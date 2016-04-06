package com.example.daniel.cd9_parent_client.networking;

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
        return CreateService(serviceClass, null);
    }

    public static <S> S CreateService(Class<S> serviceClass, String customToken) {

        // serviceClass is our client interface that defines the API endpoints for GET and POST requests

        // We need to encode the username:password in base64 so that the server can processes
            // not sure why we prepend "basic", but its specified in the http1.1 spec
        //final String base64EncodedCredentials = "Basic " + Base64.encodeToString(str.getBytes(), Base64.NO_WRAP);
        final String token = customToken;

        // Here is where we define the header information for our client
        // See curl -H ""
        // REFER HERE: https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html for http header spec
        //Log.e("SERVICE_GENERATOR", str + " " + base64EncodedCredentials);


        httpClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                // build a new request with the appropriate headers from the original request
                Request original = chain.request();
                Request.Builder requestBuilder;

                if (token == null) {
                     requestBuilder = original.newBuilder()
                            .header("Accept", "application/json")
                            .method(original.method(), original.body());
                }
                else
                {
                    requestBuilder = original.newBuilder()
                            .header("Accept", "application/json")
                            .header("Authorization", "Token " + token)
                            .method(original.method(), original.body());
                }
                //.header("Authorization", base64EncodedCredentials)
                // proceed with the chain of command, lols~~
                return chain.proceed(requestBuilder.build());
            }
        });



        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.interceptors().add(logging);

        OkHttpClient myclient = httpClient.build();
        Retrofit retrofit = builder.client(myclient).build();
        return retrofit.create(serviceClass);

    }
}
