package com.example.nitheeshkpai.nytimes;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by nitheeshkpai on 3/3/17.
 */
class MediaInfo {

    @SerializedName("media-metadata")
    private final ArrayList<ImageURL> imagesList;

    public MediaInfo(ArrayList<ImageURL> imageURLList) {
        imagesList = imageURLList;
    }

    public String getImageURL() {
        if (imagesList == null || imagesList.isEmpty()) {
            return null;
        }
        return imagesList.get(0).getURL();
    }

}
