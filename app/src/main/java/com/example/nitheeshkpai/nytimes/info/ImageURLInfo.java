package com.example.nitheeshkpai.nytimes.info;

/**
 * Created by nitheeshkpai on 3/4/17.
 * Info class to obtain url of image.
 */
class ImageURLInfo {
    private final String url;

    public ImageURLInfo(String urlWithSource) {
        this.url = urlWithSource;
    }

    public String getURL() {
        return url;
    }

}
