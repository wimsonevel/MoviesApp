package com.wimsonevel.moviesapp.task;

import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.wimsonevel.moviesapp.BuildConfig;
import com.wimsonevel.moviesapp.listener.DetailTaskListener;
import com.wimsonevel.moviesapp.model.Genre;
import com.wimsonevel.moviesapp.model.MovieDetail;
import com.wimsonevel.moviesapp.network.Constant;
import com.wimsonevel.moviesapp.network.HttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wim on 12/7/16.
 */
public class FetchMovieDetailTask extends AsyncTask<Integer, Void, MovieDetail> {

    private HttpClient httpClient;
    private DetailTaskListener detailTaskListener;

    public FetchMovieDetailTask(DetailTaskListener detailTaskListener) {
        httpClient = new HttpClient();
        this.detailTaskListener = detailTaskListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        detailTaskListener.showProgress();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        detailTaskListener.showProgress();
    }

    @Override
    protected MovieDetail doInBackground(Integer... params) {
        final String LANG_PARAM = "language";
        final String KEY_PARAM = "api_key";

        Uri builtUri = Uri.parse(Constant.MOVIE_DETAIL).buildUpon()
                .appendPath(String.valueOf(params[0]))
                .appendQueryParameter(KEY_PARAM, BuildConfig.MOVIE_API_KEY)
                .appendQueryParameter(LANG_PARAM, Constant.LANG_EN)
                .build();

        String response = httpClient.doRequest(builtUri, Constant.GET_METHOD);

        try {
            return getMovieDetailDataFromJson(response);
        } catch (JSONException e) {
            detailTaskListener.hideProgress();
            detailTaskListener.failed(e.getMessage());
            Log.e(FetchMovieDetailTask.class.getSimpleName(), e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    private MovieDetail getMovieDetailDataFromJson(String jsonStr) throws JSONException {

        if(!TextUtils.isEmpty(jsonStr)) {
            MovieDetail movieDetail = new MovieDetail();

            try {
                JSONObject detailJson = new JSONObject(jsonStr);

                int id = detailJson.getInt("id");
                String originalLanguage = detailJson.getString("original_language");
                String originalTitle = detailJson.getString("original_title");
                String overview = detailJson.getString("overview");
                String posterPath = detailJson.getString("poster_path");
                String releaseDate = detailJson.getString("release_date");
                int runtime = detailJson.getInt("runtime");
                double voteAverage = detailJson.getDouble("vote_average");
                String homepage = detailJson.getString("homepage");

                JSONArray genreArr = detailJson.getJSONArray("genres");

                List<Genre> genres = new ArrayList<>();
                Genre genre;

                for (int i = 0; i < genreArr.length(); i++) {
                    JSONObject objResult = genreArr.getJSONObject(i);

                    int genreId = objResult.getInt("id");
                    String genreName = objResult.getString("name");

                    genre = new Genre();
                    genre.setId(genreId);
                    genre.setName(genreName);

                    genres.add(genre);
                }

                movieDetail.setId(id);
                movieDetail.setOriginalLanguage(originalLanguage);
                movieDetail.setOriginalTitle(originalTitle);
                movieDetail.setOverview(overview);
                movieDetail.setPosterPath(posterPath);
                movieDetail.setReleaseDate(releaseDate);
                movieDetail.setRuntime(runtime);
                movieDetail.setVoteAverage(voteAverage);
                movieDetail.setHomepage(homepage);
                movieDetail.setGenres(genres);

                return movieDetail;
            }catch (JSONException e) {
                detailTaskListener.hideProgress();
                detailTaskListener.failed(e.getMessage());
                Log.e(FetchMovieDetailTask.class.getSimpleName(), e.getMessage(), e);
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(MovieDetail movieDetail) {
        detailTaskListener.hideProgress();
        if (movieDetail != null) {
            detailTaskListener.loadDataFinished(movieDetail);
            // New data is back from the server.  Hooray!
        }
    }
}
