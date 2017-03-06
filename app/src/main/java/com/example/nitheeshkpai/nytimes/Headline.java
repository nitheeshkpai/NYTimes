package com.example.nitheeshkpai.nytimes;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nitheeshkpai on 3/4/17.
 */
@SuppressWarnings("unused")
class Headline {

    @SerializedName("main")
    private String title;

    public String getTitle() {
        return title;
    }
}
