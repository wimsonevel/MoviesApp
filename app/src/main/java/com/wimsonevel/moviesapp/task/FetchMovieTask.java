package com.wimsonevel.moviesapp.task;

import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.wimsonevel.moviesapp.BuildConfig;
import com.wimsonevel.moviesapp.listener.TaskListener;
import com.wimsonevel.moviesapp.model.Movie;
import com.wimsonevel.moviesapp.model.MovieData;
import com.wimsonevel.moviesapp.network.Constant;
import com.wimsonevel.moviesapp.network.HttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wim on 12/5/16.
 */
public class FetchMovieTask extends AsyncTask<String, Void, Movie> {

    private HttpClient httpClient;
    private TaskListener taskListener;

    public FetchMovieTask(TaskListener taskListener) {
        httpClient = new HttpClient();
        this.taskListener = taskListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        taskListener.showProgress();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        taskListener.showProgress();
    }

    @Override
    protected Movie doInBackground(String... params) {
        // If there's no zip code, there's nothing to look up.  Verify size of params.

        final String LANG_PARAM = "language";
        final String KEY_PARAM = "api_key";
        final String PAGE = "page";

        Uri builtUri = Uri.parse(params[0]).buildUpon()
                .appendQueryParameter(KEY_PARAM, BuildConfig.MOVIE_API_KEY)
                .appendQueryParameter(LANG_PARAM, Constant.LANG_EN)
                .appendQueryParameter(PAGE, params[1])
                .build();

        String response = httpClient.doRequest(builtUri, Constant.GET_METHOD);

        try {
            return getMovieDataFromJson(response);
        } catch (JSONException e) {
            taskListener.hideProgress();
            taskListener.failed(e.getMessage());
            Log.e(FetchMovieTask.class.getSimpleName(), e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    private Movie getMovieDataFromJson(String jsonStr) throws JSONException {
        if(!TextUtils.isEmpty(jsonStr)) {
            Movie movie = new Movie();

            try {
                JSONObject movieJson = new JSONObject(jsonStr);

                int page = movieJson.getInt("page");
                JSONArray results = movieJson.getJSONArray("results");

                List<MovieData> movieDatas = new ArrayList<>();
                MovieData movieData;

                for (int i = 0; i < results.length(); i++) {
                    JSONObject objResult = results.getJSONObject(i);

                    String posterPath = objResult.getString("poster_path");
                    boolean adult = objResult.getBoolean("adult");
                    String overview = objResult.getString("overview");
                    String releaseDate = objResult.getString("release_date");

                    JSONArray genre = objResult.getJSONArray("genre_ids");

                    List<Integer> genreIds = new ArrayList<>();

                    for (int j = 0; j < genre.length(); j++) {
                        genreIds.add(genre.getInt(j));
                    }

                    int id = objResult.getInt("id");
                    String originalTitle = objResult.getString("original_title");
                    String originalLanguage = objResult.getString("original_language");
                    String title = objResult.getString("title");
                    String backdropPath = objResult.getString("backdrop_path");
                    double popularity = objResult.getDouble("popularity");
                    int voteCount = objResult.getInt("vote_count");
                    boolean video = objResult.getBoolean("video");
                    double voteAverage = objResult.getDouble("vote_average");

                    movieData = new MovieData();
                    movieData.setPosterPath(posterPath);
                    movieData.setAdult(adult);
                    movieData.setOverview(overview);
                    movieData.setReleaseDate(releaseDate);
                    movieData.setGenreIds(genreIds);
                    movieData.setId(id);
                    movieData.setOriginalTitle(originalTitle);
                    movieData.setOriginalLanguage(originalLanguage);
                    movieData.setTitle(title);
                    movieData.setBackdropPath(backdropPath);
                    movieData.setPopularity(popularity);
                    movieData.setVoteCount(voteCount);
                    movieData.setVideo(video);
                    movieData.setVoteAverage(voteAverage);

                    movieDatas.add(movieData);
                }

                movie.setPage(page);
                movie.setResults(movieDatas);

                return movie;
            } catch (JSONException e) {
                taskListener.hideProgress();
                taskListener.failed(e.getMessage());
                Log.e(FetchMovieTask.class.getSimpleName(), e.getMessage(), e);
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Movie movie) {
        taskListener.hideProgress();
        if (movie != null) {
            taskListener.loadDataFinished(movie.getResults());
            Log.e(FetchMovieTask.class.getSimpleName(), movie.getPage()+"");
            // New data is back from the server.  Hooray!
        }
    }
}
