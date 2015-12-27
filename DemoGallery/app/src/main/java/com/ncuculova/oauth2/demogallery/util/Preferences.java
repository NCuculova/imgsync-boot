package com.ncuculova.oauth2.demogallery.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Issue SharedPreferences
 * save username and password if user is signed in
 * save access and refresh token to complete authorization
 */
public class Preferences {

    private static Preferences mInstance;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private Preferences(Context context) {
        preferences = context.getSharedPreferences("img_sync", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public static Preferences getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Preferences(context);
        }
        return mInstance;
    }

    public String getAccessToken() {
        return preferences.getString("access_token", "");
    }

    public void setAccessToken(String accessToken) {
        editor.putString("access_token", accessToken);
        editor.commit();
    }

    public String getRefreshToken() {
        return preferences.getString("refresh_token", "");
    }

    public void setRefreshToken(String refreshToken) {
        editor.putString("refresh_token", refreshToken);
        editor.commit();
    }

    public boolean isSigned() {
        if (!getAccessToken().equals("")) {
            return true;
        }
        return false;
    }

    public void signOutUser(){
        editor.clear();
        editor.commit();
    }
}
