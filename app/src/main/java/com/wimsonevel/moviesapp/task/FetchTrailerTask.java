package com.wimsonevel.moviesapp.task;

import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.wimsonevel.moviesapp.BuildConfig;
import com.wimsonevel.moviesapp.listener.TaskListener;
import com.wimsonevel.moviesapp.model.Trailer;
import com.wimsonevel.moviesapp.model.TrailerData;
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
public class FetchTrailerTask extends AsyncTask<Integer, Void, Trailer> {

    private HttpClient httpClient;
    private TaskListener taskListener;

    public FetchTrailerTask(TaskListener taskListener) {
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
    protected Trailer doInBackground(Integer... params) {
        final String LANG_PARAM = "language";
        final String KEY_PARAM = "api_key";

        Uri builtUri = Uri.parse(Constant.MOVIE_TRAILER).buildUpon()
                .appendPath(String.valueOf(params[0]))
                .appendPath(Constant.VIDEOS)
                .appendQueryParameter(KEY_PARAM, BuildConfig.MOVIE_API_KEY)
                .appendQueryParameter(LANG_PARAM, Constant.LANG_EN)
                .build();

        String response = httpClient.doRequest(builtUri, Constant.GET_METHOD);

        try {
            Log.i(FetchTrailerTask.class.getSimpleName(), response);
            return getTrailerDataFromJson(response);
        } catch (JSONException e) {
            taskListener.hideProgress();
            taskListener.failed(e.getMessage());
            Log.e(FetchTrailerTask.class.getSimpleName(), e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    private Trailer getTrailerDataFromJson(String jsonStr)  throws JSONException {
        if(!TextUtils.isEmpty(jsonStr)) {
            Trailer trailer = new Trailer();

            try{
                JSONObject trailerJson = new JSONObject(jsonStr);

                int id = trailerJson.getInt("id");
                JSONArray results = trailerJson.getJSONArray("results");

                List<TrailerData> trailerDatas = new ArrayList<>();
                TrailerData trailerData;

                for (int i = 0; i < results.length(); i++) {
                    JSONObject objResult = results.getJSONObject(i);

                    String ids = objResult.getString("id");
                    String key = objResult.getString("key");
                    String name = objResult.getString("name");
                    String site = objResult.getString("site");
                    int size = objResult.getInt("size");
                    String type = objResult.getString("type");

                    trailerData = new TrailerData();
                    trailerData.setId(ids);
                    trailerData.setKey(key);
                    trailerData.setName(name);
                    trailerData.setSite(site);
                    trailerData.setSize(size);
                    trailerData.setType(type);

                    trailerDatas.add(trailerData);
                }

                trailer.setId(id);
                trailer.setResults(trailerDatas);

                return trailer;
            }catch (JSONException e) {
                taskListener.hideProgress();
                taskListener.failed(e.getMessage());
                Log.e(FetchTrailerTask.class.getSimpleName(), e.getMessage(), e);
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Trailer trailer) {
        taskListener.hideProgress();
        if (trailer != null) {
            taskListener.loadDataFinished(trailer.getResults());
            // New data is back from the server.  Hooray!
        }
    }

}
