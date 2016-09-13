package com.vetthai.app;

import android.app.Application;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by th.panya.bas on 10/14/2015.
 */
public class User extends Application {

    private  String Id;
    private String Name;
    private String imgPath;
    private String Email;
    private GoogleApiClient mGoogleApiClient;


    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }

    public void setmGoogleApiClient(GoogleApiClient mGoogleApiClient) {
        this.mGoogleApiClient = mGoogleApiClient;
    }
}
