package com.example.nitheeshkpai.nytimes.info;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by nitheeshkpai on 3/3/17.
 * Main info class used to convert News article JSON response into objects
 */
public class NewsItemInfo {

    private String title;

    @SerializedName("url")
    private String link;

    @SerializedName("description")
    private String body;

    @SerializedName("urlToImage")
    private String urlToImage;

    @SerializedName("publishedAt")
    private String publishedDate;

    public NewsItemInfo() {

    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return publishedDate;
    }

    public String getBody() {
        return body;
    }

    public String getImageURL() {
        return urlToImage;
    }

    public String getLink() {
        return link;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setDate(String date) {
        this.publishedDate = date;
    }

    public void setImageURL(String imageURL) {
        this.urlToImage = imageURL;
    }

}
