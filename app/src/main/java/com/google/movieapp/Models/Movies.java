package com.google.movieapp.Models;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.movieapp.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ahmed El-Mahdi on 3/29/2017.
 */

public class Movies implements Parcelable {

    private int id;
    private String title;
    private String poster_path;
    private String backdrop_path;
    private String overview;
    private int vote_average;
    private String release_date;

    public static final Parcelable.Creator<Movies> CREATOR
            = new Parcelable.Creator<Movies>() {
        public Movies createFromParcel(Parcel in) {
            return new Movies(in);
        }

        public Movies[] newArray(int size) {
            return new Movies[size];
        }
    };

    public int getId() {

        return id;
    }

    public String getTitle()
    {
        return title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public String getOverview() {

        return overview;
    }

    public String getRelease_date() {

        return release_date;
    }

    public int getVote_average() {
        return vote_average;
    }

    public Movies(JSONObject movie) throws JSONException {
        this.id = movie.getInt("id");
        this.title = movie.getString("original_title");
        this.poster_path = movie.getString("poster_path");
        this.backdrop_path = movie.getString("backdrop_path");
        this.overview = movie.getString("overview");
        this.vote_average = movie.getInt("vote_average");
        this.release_date = movie.getString("release_date");
    }

    public Movies(Cursor cursor) {
        this.id = cursor.getInt(MainActivity.COL_MOVIE_ID);
        this.title = cursor.getString(MainActivity.COL_TITLE);
        this.poster_path= cursor.getString(MainActivity.COL_POSTER_IMAGE);
        this.backdrop_path = cursor.getString(MainActivity.COL_BACKDROP_IMAGE);
        this.overview = cursor.getString(MainActivity.COL_OVERVIEW);
        this.vote_average = cursor.getInt(MainActivity.COL_VOTE_AVERAGE);
        this.release_date = cursor.getString(MainActivity.COL_RELEASE_DATE);
    }
    private Movies(Parcel in) {
        id = in.readInt();
        title = in.readString();
        poster_path = in.readString();
        backdrop_path = in.readString();
        overview = in.readString();
        vote_average = in.readInt();
        release_date = in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(poster_path);
        dest.writeString(backdrop_path);
        dest.writeString(overview);
        dest.writeInt(vote_average);
        dest.writeString(release_date);
    }
}