package com.example.nitheeshkpai.nytimes;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by nitheeshkpai on 3/3/17.
 */
public class MediaInfo {

    @SerializedName("media-metadata")
    private ArrayList<ImageURL> imagesList;

    private static String SAMPLE_IMAGE = "https://daks2k3a4ib2z.cloudfront.net/56e9debe633486e33019844e/56edbaee77b06a341608c1fa_the-new-york-times.png";

    public MediaInfo(ArrayList<ImageURL> imageURLList) {
        if (imageURLList == null) {
            imagesList = new ArrayList<>();
            imagesList.add(new ImageURL(SAMPLE_IMAGE));
        } else {
            imagesList = imageURLList;
        }
    }

    public String getImageURL() {
        return imagesList.get(0).getURL();
    }

}
