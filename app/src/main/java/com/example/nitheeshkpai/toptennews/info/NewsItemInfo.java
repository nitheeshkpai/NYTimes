package com.example.nitheeshkpai.toptennews.info;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nitheeshkpai on 3/3/17.
 * Main info class used to convert News article JSON response into objects
 */
public class NewsItemInfo implements Parcelable {

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

    protected NewsItemInfo(Parcel in) {
        title = in.readString();
        link = in.readString();
        body = in.readString();
        urlToImage = in.readString();
        publishedDate = in.readString();
    }

    public static final Creator<NewsItemInfo> CREATOR = new Creator<NewsItemInfo>() {
        @Override
        public NewsItemInfo createFromParcel(Parcel in) {
            return new NewsItemInfo(in);
        }

        @Override
        public NewsItemInfo[] newArray(int size) {
            return new NewsItemInfo[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(link);
        dest.writeString(body);
        dest.writeString(urlToImage);
        dest.writeString(publishedDate);
    }
}
