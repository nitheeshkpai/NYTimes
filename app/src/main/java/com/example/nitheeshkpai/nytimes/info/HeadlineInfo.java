package com.example.nitheeshkpai.nytimes.info;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nitheeshkpai on 3/4/17.
 * Info class to obtain title of news from search results.
 */
@SuppressWarnings("unused")
class HeadlineInfo {

    @SerializedName("main")
    private String title;

    public String getTitle() {
        return title;
    }
}
