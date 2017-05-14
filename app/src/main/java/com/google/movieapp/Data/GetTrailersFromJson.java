package com.google.movieapp.Data;

import com.google.movieapp.Models.Trailers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ahmed El-Mahdi on 5/1/2017.
 */

public class GetTrailersFromJson {
    public GetTrailersFromJson(String jsonStr) {
    }

    public static List<Trailers> getTrailersDataFromJson(String jsonStr) throws JSONException {
        JSONObject trailerJson = new JSONObject(jsonStr);
        JSONArray trailerArray = trailerJson.getJSONArray("results");

        List<Trailers> results = new ArrayList<>();

        for (int i = 0; i < trailerArray.length(); i++) {
            JSONObject trailer = trailerArray.getJSONObject(i);
                Trailers trailerModel = new Trailers(trailer);
                results.add(trailerModel);

        }
        return results;
    }
}
