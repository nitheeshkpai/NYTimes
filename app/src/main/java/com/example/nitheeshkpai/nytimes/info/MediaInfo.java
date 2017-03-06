package com.example.nitheeshkpai.nytimes.info;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by nitheeshkpai on 3/3/17.
 * Info class to get the list of Image URLs
 */
class MediaInfo {

    @SerializedName("media-metadata")
    private final ArrayList<ImageURLInfo> imagesList;

    public MediaInfo(ArrayList<ImageURLInfo> imageURLInfoList) {
        imagesList = imageURLInfoList;
    }

    public String getImageURL() {
        if (imagesList == null || imagesList.isEmpty()) {
            return null;
        }
        return imagesList.get(0).getURL();
    }

}
