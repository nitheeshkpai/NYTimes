package com.example.nitheeshkpai.nytimes;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by nitheeshkpai on 3/3/17.
 */
public class NewsItemInfo {

    private String title;

    @SerializedName("url")
    private String link;

    @SerializedName("abstract")
    private String body;

    @Nullable
    @SerializedName("media")
    private ArrayList<MediaInfo> media;

    @SerializedName("published_date")
    private String publishedDate;

    public NewsItemInfo() {

    }

    public NewsItemInfo(SearchResultItemInfo temp) {
        this.title = temp.getTitle();
        this.link = temp.getLink();
        this.body = temp.getBody();
        this.publishedDate = temp.getPublishedDate();
        this.media = new ArrayList<>();
        this.media.add(0, new MediaInfo(temp.getImageURL()));
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
        if (media == null || media.isEmpty()) {
            return null;
        }
        return media.get(0).getImageURL();
    }

    public String getLink() {
        return link;
    }
}
