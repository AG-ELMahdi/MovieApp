package com.google.movieapp.Data;

import com.google.movieapp.Models.Reviews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ahmed El-Mahdi on 5/1/2017.
 */

public class GetReviewsFromJson {

    public static List<Reviews> getReviewsDataFromJson(String jsonStr) throws JSONException {
        JSONObject reviewJson = new JSONObject(jsonStr);
        JSONArray reviewArray = reviewJson.getJSONArray("results");

        List<Reviews> results = new ArrayList<>();

        for (int i = 0; i < reviewArray.length(); i++) {
            JSONObject review = reviewArray.getJSONObject(i);
            Reviews reviewsModel = new Reviews(review);
            results.add(reviewsModel);
        }

        return results;
    }
}
