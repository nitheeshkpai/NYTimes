package com.example.nitheeshkpai.nytimes;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by nitheeshkpai on 3/4/17.
 */
public class SearchResultItemInfo {

    @SerializedName("headline")
    private Headline headline;

    @SerializedName("web_url")
    private String link;

    @SerializedName("snippet")
    private String body;

    @SerializedName("multimedia")
    private ArrayList<ImageURL> imagesInfoList;

    @SerializedName("pub_date")
    private String publishedDate;

    private static String IMAGE_SOURCE = "http://www.nytimes.com/";


    public String getTitle() {
        return headline.getTitle();
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

    public ArrayList<ImageURL> getImageURL() {
        if(imagesInfoList.isEmpty()){
            return null;
        }
        ArrayList<ImageURL> urlWithSourceList = new ArrayList<>();
        for(ImageURL temp : imagesInfoList){
            urlWithSourceList.add(new ImageURL(IMAGE_SOURCE+temp.getURL()));
        }
        return urlWithSourceList;
    }
}
