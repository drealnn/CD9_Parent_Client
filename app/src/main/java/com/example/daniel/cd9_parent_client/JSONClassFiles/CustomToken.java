package com.example.daniel.cd9_parent_client.JSONClassFiles;

/**
 * Created by daniel on 1/28/16.
 */

// object to be serialized into json
// this could be turned into a generic single name:value pair for all tokens
public class CustomToken {
    private String token;
    private boolean success;
    private String id;

    public String getId() {
        return id;
    }


    public boolean isSuccess() {
        return success;
    }

    public CustomToken(String token) {
        this.token = token;
    }

    public String getToken()
    {
        return token;
    }
    public void setToken(String token) {this.token = token;}
}
