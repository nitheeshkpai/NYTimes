package com.example.nitheeshkpai.nytimes.info;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by nitheeshkpai on 3/4/17.
 * Info class that handles Image URLs in search case
 */
@SuppressWarnings("unused")
class MultimediaInfo {

    @SerializedName("url")
    private ArrayList<ImageURLInfo> ImagesList;
}
