package com.grabtaxi.grabmovie.server;

import com.grabtaxi.grabmovie.model.MovieDetailInfo;
import com.grabtaxi.grabmovie.model.MovieInfo;

import java.util.List;

import retrofit.RetrofitError;
import retrofit.http.GET;
import retrofit.http.Path;

public interface MovieServerEndpoint {

    @GET("/movie/now_playing")
    List<MovieInfo> getNowPlayingMovies() throws RetrofitError;

    @GET("/movie/{id}")
    MovieDetailInfo getMovieDetailInfo(@Path("id") int movieId) throws RetrofitError;

    @GET("/movie/{id}/similar")
    List<MovieInfo> getRelatedMovies(@Path("id") int movieId) throws RetrofitError;

}
