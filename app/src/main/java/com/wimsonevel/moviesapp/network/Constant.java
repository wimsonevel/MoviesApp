package com.wimsonevel.moviesapp.network;

import com.wimsonevel.moviesapp.BuildConfig;

/**
 * Created by Wim on 12/5/16.
 */
public class Constant {

    public static final String BASE_URL = BuildConfig.BASE_URL;
    public static final String IMG_URL = BuildConfig.IMG_URL;
    public static final String VERSION = "/3";
    public static final String MOVIE = "/movie";
    public static final String VIDEOS = "videos";
    public static final String REVIEWS = "reviews";
    public static final String LANG_EN = "en-US";

    public static final String GET_METHOD = "GET";

    public static final String MOVIE_POPULAR = BASE_URL + VERSION + MOVIE + "/popular?";
    public static final String MOVIE_TOP_RATED = BASE_URL + VERSION + MOVIE + "/top_rated?";
    public static final String MOVIE_TRAILER = BASE_URL + VERSION + MOVIE;
    public static final String MOVIE_REVIEWS = BASE_URL + VERSION + MOVIE;
    public static final String MOVIE_DETAIL = BASE_URL + VERSION + MOVIE;

}
