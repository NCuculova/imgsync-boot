package com.ncuculova.oauth2.demogallery;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.ncuculova.oauth2.demogallery.model.Image;
import com.ncuculova.oauth2.demogallery.util.DemoGalleryHttpClient;
import com.ncuculova.oauth2.demogallery.util.HttpLoader;
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
public class ImagesLoader extends HttpLoader<List<Image>> {

    DemoGalleryHttpClient mClient;
    long mAlbumId;

    public ImagesLoader(Context context, long id) {
        super(context);
        mClient = DemoGalleryHttpClient.getInstance(context);
        mAlbumId = id;
    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();
        final List<Image> images = new ArrayList<>();
        mClient.getImages(mAlbumId, new DemoGalleryHttpClient.ResponseHandler() {
            @Override
            public void onSuccessJsonArray(JSONArray jsonArray) {
                super.onSuccessJsonArray(jsonArray);
                try {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject data = (JSONObject) jsonArray.get(i);
                        images.add(JSONParser.parseImage(data));
                    }
                    deliverResult(images);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
