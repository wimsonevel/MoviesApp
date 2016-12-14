package com.wimsonevel.moviesapp.task;

import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.wimsonevel.moviesapp.BuildConfig;
import com.wimsonevel.moviesapp.listener.TaskListener;
import com.wimsonevel.moviesapp.model.Review;
import com.wimsonevel.moviesapp.model.ReviewData;
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
public class FetchReviewTask extends AsyncTask<Integer, Void, Review> {

    private HttpClient httpClient;
    private TaskListener taskListener;

    public FetchReviewTask(TaskListener taskListener) {
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
    protected Review doInBackground(Integer... params) {
        final String LANG_PARAM = "language";
        final String KEY_PARAM = "api_key";

        Uri builtUri = Uri.parse(Constant.MOVIE_REVIEWS).buildUpon()
                .appendPath(String.valueOf(params[0]))
                .appendPath(Constant.REVIEWS)
                .appendQueryParameter(KEY_PARAM, BuildConfig.MOVIE_API_KEY)
                .appendQueryParameter(LANG_PARAM, Constant.LANG_EN)
                .build();

        String response = httpClient.doRequest(builtUri, Constant.GET_METHOD);

        try {
            return getReviewDataFromJson(response);
        } catch (JSONException e) {
            taskListener.hideProgress();
            taskListener.failed(e.getMessage());
            Log.e(FetchTrailerTask.class.getSimpleName(), e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    private Review getReviewDataFromJson(String jsonStr) throws JSONException {

        if(!TextUtils.isEmpty(jsonStr)) {
            Review review = new Review();

            try {
                JSONObject reviewJson = new JSONObject(jsonStr);

                int id = reviewJson.getInt("id");
                int page = reviewJson.getInt("page");

                JSONArray results = reviewJson.getJSONArray("results");

                List<ReviewData> reviewDatas = new ArrayList<>();
                ReviewData reviewData;

                for (int i = 0; i < results.length(); i++) {
                    JSONObject objResult = results.getJSONObject(i);

                    String ids = objResult.getString("id");
                    String author = objResult.getString("author");
                    String content = objResult.getString("content");
                    String url = objResult.getString("url");

                    reviewData = new ReviewData();
                    reviewData.setId(ids);
                    reviewData.setAuthor(author);
                    reviewData.setContent(content);
                    reviewData.setUrl(url);

                    reviewDatas.add(reviewData);
                }

                int totalPages = reviewJson.getInt("total_pages");
                int totalResults = reviewJson.getInt("total_results");

                review.setId(id);
                review.setPage(page);
                review.setResults(reviewDatas);
                review.setTotalPages(totalPages);
                review.setTotalResults(totalResults);

                return review;
            }catch (JSONException e) {
                taskListener.hideProgress();
                taskListener.failed(e.getMessage());
                Log.e(FetchReviewTask.class.getSimpleName(), e.getMessage(), e);
                e.printStackTrace();
            }
        }

        return null;
    }
    @Override
    protected void onPostExecute(Review review) {
        taskListener.hideProgress();
        if (review != null) {
            taskListener.loadDataFinished(review.getResults());
            // New data is back from the server.  Hooray!
        }
    }
}
