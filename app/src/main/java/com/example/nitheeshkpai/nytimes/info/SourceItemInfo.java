package com.example.nitheeshkpai.nytimes.info;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nitheeshkpai on 3/8/17.
 * Class that handles all DB stuff used in Save Article feature
 */
public class SourceItemInfo {
    
    private String id;

    private String name;

    private String url;

    @SerializedName("urlsToLogos")
    public URLContainer urlContainer;

    public String getTitle() {
        return name;
    }

    public String getLink() {
        return url;
    }

    public String getId() {
        return id;
    }
}
