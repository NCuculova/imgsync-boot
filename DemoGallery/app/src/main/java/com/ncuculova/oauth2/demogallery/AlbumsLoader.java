package com.ncuculova.oauth2.demogallery;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.ncuculova.oauth2.demogallery.model.Album;
import com.ncuculova.oauth2.demogallery.util.DemoGalleryHttpClient;
import com.ncuculova.oauth2.demogallery.util.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class AlbumsLoader extends AsyncTaskLoader<List<Album>> {

    long mUserId;
    DemoGalleryHttpClient mClient;

    public AlbumsLoader(Context context, long userId) {
        super(context);
        mUserId = userId;
        mClient = DemoGalleryHttpClient.getInstance(context);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public List<Album> loadInBackground() {
        final List<Album> albums = new ArrayList<>();
        mClient.getAlbumsSync(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject data = (JSONObject) response.get(i);
                        albums.add(JSONParser.parseAlbum(data));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        return albums;
    }
}