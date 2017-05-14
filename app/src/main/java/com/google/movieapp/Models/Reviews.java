package com.google.movieapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ahmed El-Mahdi on 4/30/2017.
 */

public class Reviews implements Parcelable{

    private String id;
    private String mAuthor;
    private String mContent;



    private String mUrl;

    public Reviews(JSONObject reviewObject) throws JSONException {
        this.id = reviewObject.getString("id");
        this.mAuthor=reviewObject.getString("author");
        this.mContent=reviewObject.getString("content");
        this.mUrl = reviewObject.getString("url");
    }

    protected Reviews(Parcel in) {
        id = in.readString();
        mAuthor = in.readString();
        mContent = in.readString();
        mUrl = in.readString();
    }

    public static final Creator<Reviews> CREATOR = new Creator<Reviews>() {
        @Override
        public Reviews createFromParcel(Parcel in) {
            return new Reviews(in);
        }

        @Override
        public Reviews[] newArray(int size) {
            return new Reviews[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public String getmContent() {
        return mContent;
    }

    public String getmUrl() {
        return mUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(mAuthor);
        dest.writeString(mContent);
        dest.writeString(mUrl);
    }
}
