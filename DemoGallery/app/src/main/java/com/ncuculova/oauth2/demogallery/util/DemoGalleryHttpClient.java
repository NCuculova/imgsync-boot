package com.ncuculova.oauth2.demogallery.util;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.ncuculova.oauth2.demogallery.model.Image;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import cz.msebera.android.httpclient.Header;

/**
 * HTTP client for making API calls only after it is authorized
 */
public class DemoGalleryHttpClient {

    public static final String BASE_URI = "http://553b42aa.ngrok.io";
    private static final String CLIENT_ID = "img_sync";
    private static final String CLIENT_SECRET = "img_sync_secret";
    public static final String TOKEN_ENDPOINT = "/oauth/token";
    public static final String TAG = "DemoGalleryHttpClient";

    private static DemoGalleryHttpClient mClient;
    AsyncHttpClient mHttpClient;
    static Preferences mPreferences;

    private DemoGalleryHttpClient(Context context) {
        this.mHttpClient = new AsyncHttpClient();
        mPreferences = Preferences.getInstance(context);
    }

    public static DemoGalleryHttpClient getInstance(Context context) {
        if (mClient == null) {
            mClient = new DemoGalleryHttpClient(context);
        }
        return mClient;
    }

    public void authorize() {
        mHttpClient.addHeader("Authorization", String.format("Bearer %s", mPreferences.getAccessToken()));
    }

    public void cancelAllRequests() {
        mHttpClient.cancelAllRequests(true);
    }

    public void getAccessToken(String email, String password, final OnResponse onResponse) {
        AsyncHttpClient httpClient = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("grant_type", "password");
        requestParams.put("username", email);
        requestParams.put("password", password);
        httpClient.setBasicAuth(CLIENT_ID, CLIENT_SECRET);
        httpClient.post(BASE_URI + TOKEN_ENDPOINT, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d(TAG, "Access token obtained!");
                onResponse.onSuccessJsonObject(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                onResponse.onFailureJsonArray(statusCode, errorResponse);
            }
        });
    }

    public void getAccessTokenFromRefreshToken(final OnResponse onResponse) {
        AsyncHttpClient httpClient = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("grant_type", "refresh_token");
        System.out.println("REFRESH TOKEN: " + mPreferences.getRefreshToken());
        requestParams.put("refresh_token", mPreferences.getRefreshToken());
        httpClient.setBasicAuth(CLIENT_ID, CLIENT_SECRET);
        httpClient.post(BASE_URI + TOKEN_ENDPOINT, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    mPreferences.setAccessToken(response.getString("access_token"));
                    onResponse.onSuccessJsonObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d(TAG, "Get token from refresh token fail: " + errorResponse.toString());
                onResponse.onFailureJsonObject(statusCode, errorResponse);
            }
        });
    }

    public void get(final String url, final OnResponse onResponse) {
        authorize();
        Log.d(TAG, "GET Request: " + url);
        mHttpClient.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                onResponse.onSuccessJsonObject(response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                onResponse.onSuccessJsonArray(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                onResponse.onSuccessJsonArray(errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (statusCode == 401) {
                    Log.d(TAG, "Unauthorized, invalid token");
                    mClient.getAccessTokenFromRefreshToken(new ResponseHandler() {
                        @Override
                        public void onSuccessJsonObject(JSONObject jsonObject) {
                            super.onSuccessJsonObject(jsonObject);
                            get(url, onResponse);
                        }
                    });
                }
                onResponse.onFailureJsonObject(statusCode, errorResponse);

            }
        });
    }

    public void post(final String url, final RequestParams requestParams, final OnResponse onResponse) {
        authorize();
        Log.d(TAG, "POST Request: " + url);
        mHttpClient.post(url, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                onResponse.onSuccessJsonObject(response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                onResponse.onSuccessJsonArray(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                onResponse.onSuccessJsonArray(errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (statusCode == 401) {
                    Log.d(TAG, "Unauthorized, invalid token");
                    mClient.getAccessTokenFromRefreshToken(new ResponseHandler() {
                        @Override
                        public void onSuccessJsonObject(JSONObject jsonObject) {
                            super.onSuccessJsonObject(jsonObject);
                            post(url, requestParams, onResponse);
                        }
                    });
                }
                onResponse.onFailureJsonObject(statusCode, errorResponse);
            }
        });
    }

    public void delete(final String url, final OnResponse onResponse) {
        authorize();
        Log.d(TAG, "DELETE Request: " + url);
        mHttpClient.delete(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                onResponse.onSuccessJsonObject(response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                onResponse.onSuccessJsonArray(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                onResponse.onFailureJsonArray(statusCode, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (statusCode == 401) {
                    Log.d(TAG, "Unauthorized, invalid token");
                    mClient.getAccessTokenFromRefreshToken(new ResponseHandler() {
                        @Override
                        public void onSuccessJsonObject(JSONObject jsonObject) {
                            super.onSuccessJsonObject(jsonObject);
                            delete(url, onResponse);
                        }
                    });
                    onResponse.onFailureJsonObject(statusCode, errorResponse);
                }
            }
        });
    }

    public void dummyRequest(OnResponse onResponse){
        Log.d(TAG, "obtain valid access token");
        get(String.format("%s/api/", BASE_URI), onResponse);
    }
    public void signUpUser(String email, String password, OnResponse onResponse) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("username", email);
        requestParams.put("password", password);
        post(String.format("%s/api/sign_up", BASE_URI), requestParams, onResponse);
    }

    public void createAlbum(String albumName, OnResponse onResponse) {
        RequestParams params = new RequestParams();
        params.put("name", albumName);
        post(String.format("%s/api/albums", BASE_URI), params, onResponse);
    }

    public void updateAlbum(String albumName, long albumId, JsonHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("name", albumName);
        authorize();
        mHttpClient.put(String.format("%s/api/albums/%d", BASE_URI, albumId), params, responseHandler);
    }

    public void getAlbums(OnResponse onResponse) {
        get(String.format("%s/api/albums/my", BASE_URI), onResponse);
    }

    public void getImages(long albumId, OnResponse onResponse) {
        get(String.format("%s/api/albums/%d/images", BASE_URI, albumId), onResponse);
    }

    public void uploadImage(long albumId, Image image, InputStream inputStream, OnResponse onResponse) {
        RequestParams params = new RequestParams();
        params.put("file", inputStream, image.name, image.mimeType);
        post(String.format("%s/api/albums/%d/image", BASE_URI, albumId), params, onResponse);
    }

    public void deleteImage(long albumId, long imgId, ResponseHandler responseHandler) {
        delete(String.format("%s/api/album/%d/image/%d", BASE_URI, albumId, imgId), responseHandler);
    }

    public void deleteAlbum(long albumId, ResponseHandler responseHandler) {
        delete(String.format("%s/api/albums/%d", BASE_URI, albumId), responseHandler);
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

    public interface OnResponse {

        void onSuccessJsonObject(JSONObject jsonObject);

        void onSuccessJsonArray(JSONArray jsonArray);

        void onFailureJsonObject(int statusCode, JSONObject jsonObject);

        void onFailureJsonArray(int statusCode, JSONArray jsonArray);
    }

    public static class ResponseHandler implements OnResponse {

        @Override
        public void onSuccessJsonObject(JSONObject jsonObject) {

        }

        @Override
        public void onSuccessJsonArray(JSONArray jsonArray) {

        }

        @Override
        public void onFailureJsonObject(int statusCode, JSONObject jsonObject) {

        }

        @Override
        public void onFailureJsonArray(int statusCode, JSONArray jsonArray) {

        }
    }
}