package com.example.nitheeshkpai.nytimes.info;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by nitheeshkpai on 3/4/17.
 * Info class that converts search JSON response to objects
 */
@SuppressWarnings("unused")
public class SearchResultItemInfo {

    @SerializedName("headline")
    private HeadlineInfo headlineInfo;

    @SerializedName("web_url")
    private String link;

    @SerializedName("snippet")
    private String body;

    @SerializedName("multimedia")
    private ArrayList<ImageURLInfo> imagesInfoList;

    @SerializedName("pub_date")
    private String publishedDate;


    public String getTitle() {
        return headlineInfo.getTitle();
    }

    public String getLink() {
        return link;
    }

    public String getBody() {
        return body;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public ArrayList<ImageURLInfo> getImageURL() {
        if (imagesInfoList.isEmpty()) {
            return null;
        }
        ArrayList<ImageURLInfo> urlWithSourceList = new ArrayList<>();
        for (ImageURLInfo temp : imagesInfoList) {
            String IMAGE_SOURCE = "http://www.nytimes.com/";
            urlWithSourceList.add(new ImageURLInfo(IMAGE_SOURCE + temp.getURL()));
        }
        return urlWithSourceList;
    }
}
