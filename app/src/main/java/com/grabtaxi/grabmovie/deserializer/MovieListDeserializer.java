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
import com.grabtaxi.grabmovie.model.MovieInfo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MovieListDeserializer implements JsonDeserializer<List<MovieInfo>> {

    private static final String TAG = MovieListDeserializer.class.getSimpleName();

    @Override
    public List<MovieInfo> deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        Log.d(TAG, "deserialize");
        JsonObject jsonObject = json.getAsJsonObject();
        JsonArray arrJsonMovies = jsonObject.get("results").getAsJsonArray();

        ArrayList<MovieInfo> movies = new Gson().fromJson(arrJsonMovies.toString(), new TypeToken<List<MovieInfo>>(){}.getType());
        for (MovieInfo m : movies) {
            Log.i(TAG, m.toString());
        }
        return movies;
    }
}
