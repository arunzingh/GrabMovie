package com.grabtaxi.grabmovie.deserializer;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.grabtaxi.grabmovie.model.MovieDetailInfo;
import com.grabtaxi.grabmovie.model.MovieInfo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MovieDetailDeserializer implements JsonDeserializer<MovieDetailInfo>  {

    private static final String TAG = MovieDetailDeserializer.class.getSimpleName();

    private static final int MAX_GENRES_COUNT = 3;

    @Override
    public MovieDetailInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Log.d(TAG, "deserialize");

        JsonObject jsonObject = json.getAsJsonObject();

        MovieDetailInfo movieDetail = new MovieDetailInfo();

        movieDetail.setBackdropPath(jsonObject.get("backdrop_path").getAsString());
        movieDetail.setPosterPath(jsonObject.get("poster_path").getAsString());
        movieDetail.setBudget(jsonObject.get("budget").getAsString());
        movieDetail.setId(jsonObject.get("id").getAsInt());
        movieDetail.setOverview(jsonObject.get("overview").getAsString());
        movieDetail.setReleaseDate(jsonObject.get("release_date").getAsString());
        movieDetail.setRevenue(jsonObject.get("revenue").getAsInt());
        movieDetail.setRuntime(jsonObject.get("runtime").getAsString());
        movieDetail.setTagline(jsonObject.get("tagline").getAsString());
        movieDetail.setTitle(jsonObject.get("title").getAsString());

        JsonArray arrJsonGenres = jsonObject.get("genres").getAsJsonArray();

        int count = arrJsonGenres.size() > MAX_GENRES_COUNT ? MAX_GENRES_COUNT : arrJsonGenres.size();
        String[] genres = new String[count];
        for (int i = 0; i < count; i++) {
            JsonElement genreElement = arrJsonGenres.get(i);
            genres[i] = genreElement.getAsJsonObject().get("name").getAsString();
        }
        movieDetail.setGenres(genres);

        Log.d(TAG, movieDetail.toString());

        return movieDetail;
    }
}
