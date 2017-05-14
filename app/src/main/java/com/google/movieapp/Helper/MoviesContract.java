package com.google.movieapp.Helper;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Ahmed El-Mahdi on 5/9/2017.
 */

public class MoviesContract {
    public static final String CONTENT_AUTHORITY = "com.google.movieapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIE = "movie";

    public static final class MoviesEntry implements BaseColumns {

        public static final String TABLE_NAME = "movie";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_IMAGE = "poster_image";
        public static final String COLUMN_BACKDROP_IMAGE = "backdrop_image";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_RELEASE_DATE = "release_date";


        public static Uri buildMovieUri(long id) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(id)).build();
        }
    }
}
