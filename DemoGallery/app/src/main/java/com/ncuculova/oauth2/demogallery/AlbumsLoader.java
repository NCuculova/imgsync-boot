package com.ncuculova.oauth2.demogallery;

import android.content.Context;

import com.ncuculova.oauth2.demogallery.model.Album;
import com.ncuculova.oauth2.demogallery.util.DemoGalleryHttpClient;
import com.ncuculova.oauth2.demogallery.util.HttpLoader;
import com.ncuculova.oauth2.demogallery.util.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AlbumsLoader extends HttpLoader<List<Album>> {

    long mUserId;
    DemoGalleryHttpClient mClient;

    public AlbumsLoader(Context context, long userId) {
        super(context);
        mUserId = userId;
        mClient = DemoGalleryHttpClient.getInstance(context);
    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();
        final List<Album> albums = new ArrayList<>();
        mClient.getAlbums(new DemoGalleryHttpClient.ResponseHandler() {

            @Override
            public void onSuccessJsonArray(JSONArray jsonArray) {
                super.onSuccessJsonArray(jsonArray);
                try {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject data = (JSONObject) jsonArray.get(i);
                        albums.add(JSONParser.parseAlbum(data));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                deliverResult(albums);
            }
        });
    }
}