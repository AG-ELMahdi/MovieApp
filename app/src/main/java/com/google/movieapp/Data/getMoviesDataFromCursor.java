package com.google.movieapp.Data;

import android.database.Cursor;

import com.google.movieapp.Models.Movies;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ahmed El-Mahdi on 5/12/2017.
 */
public class GetMoviesDataFromCursor {
    public static List<Movies> getFavoriteMoviesDataFromCursor(Cursor cursor) {
        List<Movies> results = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Movies movie = new Movies(cursor);
                results.add(movie);
            } while (cursor.moveToNext());
            cursor.close();
        }
        cursor.close();
        return results;
    }
}
