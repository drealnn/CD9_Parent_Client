package com.example.daniel.cd9_parent_client.JSONClassFiles;

/**
 * Created by daniel on 1/21/16.
 */

// object to be serialized into json
    // this could be turned into a generic single name:value pair for all tokens
public class FacebookToken {

    private String token;
    private String password;

    public FacebookToken(String token)
    {
        this.token = token;
    }

    public FacebookToken(String token, String password)
    {
        this.token = token;
        this.password = password;
    }
}
