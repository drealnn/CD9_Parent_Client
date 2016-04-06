package com.example.daniel.cd9_parent_client.networking;


import android.app.Application;

import com.example.daniel.cd9_parent_client.JSONClassFiles.CustomToken;
import com.example.daniel.cd9_parent_client.JSONClassFiles.FacebookToken;
import com.example.daniel.cd9_parent_client.JSONClassFiles.GcmToken;
import com.example.daniel.cd9_parent_client.JSONClassFiles.Parent;
import com.example.daniel.cd9_parent_client.JSONClassFiles.Text;
import com.example.daniel.cd9_parent_client.JSONClassFiles.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by daniel on 1/19/16.
 */
public interface CD9ClientInterface {
    // define api endpoints here with appropriate http request


    // Call with @body should use the gson converter to appropriately convert FacebookToken to json
    // the data type between the brackets indicates how to convert the json response from the server
        // into a normal java object
    @GET("/CD9/api/get_ids/")
    Call<User[]> getIds();

    @POST("/CD9/api/new_user/")
    Call<CustomToken> createUser(@Body FacebookToken token);

    @POST("/CD9/api/update_token/")
    Call<CustomToken> updateUser(@Body FacebookToken token);

    @POST("/CD9/api/add_parent/")
    Call<Parent> addParent(@Body Parent parent);

    @POST("/CD9/api/texts/")
    Call<Text[]> sendTexts(@Body Text[] texts);

    @POST("/CD9/api/apps/")
    Call<Application[]> sendApps(@Body Application[] apps);

    @PATCH("CD9/api/update_profile/{id}/")
    Call<GcmToken> sendGcmToken(@Path("id") String id, @Body GcmToken idToken);





    // used for updating entries
    /*
    @FormUrlEncoded
    @POST("/CD9/api/users/new")
    Call<User> createUser(@Field("token_string") String tokenString, @Field("app_id") String appID ... etc);
     */
}
