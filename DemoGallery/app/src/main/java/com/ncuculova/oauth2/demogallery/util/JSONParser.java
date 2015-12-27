package com.ncuculova.oauth2.demogallery.util;

import com.ncuculova.oauth2.demogallery.model.Album;
import com.ncuculova.oauth2.demogallery.model.Image;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ncuculova on 6.12.15.
 */
public class JSONParser {

    public static Album parseAlbum(JSONObject data) {
        Album album = new Album();
        try {
            album.name = data.getString("name");
            album.id = data.getLong("id");
            album.dateCreated = data.getString("dateCreated");
            if (data.isNull("latestImageId")) {
                album.coverImageId = 0;
            } else {
                album.coverImageId = data.getLong("latestImageId");
            }
            album.numberImages = data.getInt("numberImages");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return album;
    }

    public static Image parseImage(JSONObject data) {
        Image image = new Image();
        try {
            image.name = data.getString("fileName");
            image.id = data.getLong("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return image;
    }
}
