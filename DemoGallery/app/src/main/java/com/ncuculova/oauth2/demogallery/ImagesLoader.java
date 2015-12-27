package com.ncuculova.oauth2.demogallery;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.ncuculova.oauth2.demogallery.model.Image;
import com.ncuculova.oauth2.demogallery.util.DemoGalleryHttpClient;
import com.ncuculova.oauth2.demogallery.util.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by ncuculova on 11.12.15.
 */
public class ImagesLoader extends AsyncTaskLoader<List<Image>> {

    DemoGalleryHttpClient mClient;
    long mAlbumId;

    public ImagesLoader(Context context, long id) {
        super(context);
        mClient = DemoGalleryHttpClient.getInstance(context);
        mAlbumId = id;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public List<Image> loadInBackground() {
        final List<Image> images = new ArrayList<>();
        mClient.getImagesSync(mAlbumId, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject data = (JSONObject) response.get(i);
                        images.add(JSONParser.parseImage(data));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        return images;
    }
}
