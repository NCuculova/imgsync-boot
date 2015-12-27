package com.ncuculova.oauth2.demogallery.util;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.ncuculova.oauth2.demogallery.model.Image;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;

/**
 * HTTP client for making API calls
 */
public class DemoGalleryHttpClient {

    public static final String BASE_URI = "http://2a432668.ngrok.io";
    private static final String CLIENT_ID = "img_sync";
    private static final String CLIENT_SECRET = "img_sync_secret";
    public static final String TOKEN_ENDPOINT = "/oauth/token";

    private static DemoGalleryHttpClient mClient;
    AsyncHttpClient mHttpClient;
    SyncHttpClient mSyncHttpClient;
    static Preferences mPreferences;

    private DemoGalleryHttpClient(Context context) {
        this.mHttpClient = new AsyncHttpClient();
        mSyncHttpClient = new SyncHttpClient();
        mPreferences = Preferences.getInstance(context);
    }

    public static DemoGalleryHttpClient getInstance(Context context) {
        if (mClient == null) {
            mClient = new DemoGalleryHttpClient(context);
        }
        return mClient;
    }

    public void signUpUser(String email, String password, JsonHttpResponseHandler responseHandler) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("username", email);
        requestParams.put("password", password);
        mHttpClient.post(String.format("%s/api/sign_up", BASE_URI), requestParams, responseHandler);
    }

    public void getAccessToken(String email, String password, JsonHttpResponseHandler responseHandler) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("grant_type", "password");
        requestParams.put("username", email);
        requestParams.put("password", password);
        mHttpClient.setBasicAuth(CLIENT_ID, CLIENT_SECRET);
        mHttpClient.post(BASE_URI + TOKEN_ENDPOINT, requestParams, responseHandler);
    }

    public void createAlbum(String albumName, JsonHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("name", albumName);
        mHttpClient.addHeader("Authorization", String.format("Bearer %s", mPreferences.getAccessToken()));
        mHttpClient.post(String.format("%s/api/albums", BASE_URI), params, responseHandler);
    }

    public void updateAlbum(String albumName, long albumId, JsonHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("name", albumName);
        mHttpClient.addHeader("Authorization", String.format("Bearer %s", mPreferences.getAccessToken()));
        mHttpClient.put(String.format("%s/api/albums/%d", BASE_URI, albumId), params, responseHandler);
    }

    public void getAlbumsSync(JsonHttpResponseHandler responseHandler) {
        mSyncHttpClient.addHeader("Authorization", String.format("Bearer %s", mPreferences.getAccessToken()));
        mSyncHttpClient.get(String.format("%s/api/albums/my", BASE_URI), responseHandler);
    }

    public void getImagesSync(long albumId, JsonHttpResponseHandler responseHandler) {
        mSyncHttpClient.addHeader("Authorization", String.format("Bearer %s", mPreferences.getAccessToken()));
        mSyncHttpClient.get(String.format("%s/api/albums/%d/images", BASE_URI, albumId), responseHandler);
    }

    public void uploadImage(long albumId, Image image, InputStream inputStream, JsonHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("file", inputStream, image.name, image.mimeType);
        mHttpClient.addHeader("Authorization", String.format("Bearer %s", mPreferences.getAccessToken()));
        mHttpClient.post(String.format("%s/api/albums/%d/image", BASE_URI, albumId), params, responseHandler);
    }

    public void deleteImage(long albumId, long imgId, JsonHttpResponseHandler responseHandler) {
        mHttpClient.addHeader("Authorization", String.format("Bearer %s", mPreferences.getAccessToken()));
        mHttpClient.delete(String.format("%s/api/album/%d/image/%d", BASE_URI, albumId, imgId), responseHandler);
    }

    public void deleteAlbum(long albumId, JsonHttpResponseHandler responseHandler) {
        mHttpClient.addHeader("Authorization", String.format("Bearer %s", mPreferences.getAccessToken()));
        mHttpClient.delete(String.format("%s/api/albums/%d", BASE_URI, albumId), responseHandler);
    }

    public static Picasso getPicassoClient(Context context) {
        Picasso.Builder builder = new Picasso.Builder(context);
        OkHttpClient httpClient = new OkHttpClient();
        httpClient.networkInterceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", String.format("Bearer %s", mPreferences.getAccessToken()))
                        .build();
                return chain.proceed(newRequest);
            }
        });
        Picasso picasso = builder.downloader(
                new OkHttpDownloader(httpClient)).build();
        return picasso;
    }
}