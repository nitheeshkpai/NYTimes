package com.example.nitheeshkpai.toptennews.info;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nitheeshkpai on 3/8/17.
 * Class that handles all DB stuff used in Save Article feature
 */
public class URLContainer {

    @SerializedName("small")
    private String logoImageUrl;

    public String getLogoImageUrl() {
        return logoImageUrl;
    }
}
