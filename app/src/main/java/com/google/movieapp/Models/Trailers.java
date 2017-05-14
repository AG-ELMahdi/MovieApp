package com.google.movieapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ahmed El-Mahdi on 4/30/2017.
 */

public class Trailers implements Parcelable{


    private String id;
    private String mKey;
    private String mName;
    private String mSite;
    private String mType;

    public Trailers(JSONObject trailerObject) throws JSONException {
        this.id = trailerObject.getString("id");
        this.mKey = trailerObject.getString("key");
        this.mName = trailerObject.getString("name");
        this.mSite = trailerObject.getString("site");
        this.mType = trailerObject.getString("type");
    }

    protected Trailers(Parcel in) {
        id = in.readString();
        mKey = in.readString();
        mName = in.readString();
        mSite = in.readString();
        mType = in.readString();
    }


    public static final Creator<Trailers> CREATOR = new Creator<Trailers>() {
        @Override
        public Trailers createFromParcel(Parcel in) {
            return new Trailers(in);
        }

        @Override
        public Trailers[] newArray(int size) {
            return new Trailers[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getmKey() {
        return mKey;
    }

    public String getmName() {
        return mName;
    }

    public String getmSite() {
        return mSite;
    }

    public String getMType() {
        return mType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(mKey);
        dest.writeString(mName);
        dest.writeString(mSite);
        dest.writeString(mType);
    }
}
