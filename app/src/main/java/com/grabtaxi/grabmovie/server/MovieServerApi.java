package com.grabtaxi.grabmovie.server;

import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.grabtaxi.grabmovie.deserializer.MovieDetailDeserializer;
import com.grabtaxi.grabmovie.deserializer.MovieListDeserializer;
import com.grabtaxi.grabmovie.model.MovieDetailInfo;
import com.grabtaxi.grabmovie.model.MovieInfo;

import java.util.List;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;


public class MovieServerApi {

    private static final String TAG = MovieServerApi.class.getSimpleName();

    public static final String BASE_URL = "http://api.themoviedb.org/3";


    public MovieServerApi() {
    }

    public MovieServerEndpoint getEndpoint() {

        Log.d(TAG, "getEndpoint");

        RestAdapter.Builder restAdapterBuilder = new RestAdapter.Builder()
                .setEndpoint(BASE_URL)
                .setConverter(new GsonConverter(buildGson()))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setRequestInterceptor(getRequestInterceptor());

        return restAdapterBuilder.build().create(MovieServerEndpoint.class);
    }

    private RequestInterceptor getRequestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Accept", "application/json");
                request.addQueryParam("api_key", "489597e1cc7d9b3455ff12256353c0a7");
            }
        };
    }

    private Gson buildGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        builder.registerTypeAdapter(new TypeToken<List<MovieInfo>>() {
        }.getType(), new MovieListDeserializer());
        builder.registerTypeAdapter(MovieDetailInfo.class, new MovieDetailDeserializer());
        return builder.create();
    }
}