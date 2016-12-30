package com.frankyenshaw.movielistingproject.Controllers.Http;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.frankyenshaw.movielistingproject.Controllers.Listeners.OnApiCallCompleted;
import com.frankyenshaw.movielistingproject.Models.IntentItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Public class to be re used for downloading JSON response using library OkHttp.
 * Currently using AsyncDownloader for all Api Calls
 * Would most likely create additional classes for different scenarios
 */

public class AsyncDownloader extends AsyncTask<String, Integer, String> {

    public static final String TAG = AsyncDownloader.class.getSimpleName();

    private Context context;
    private IntentItem intentItem;
    private ProgressDialog dialog;
    private String title;

    private OnApiCallCompleted listener;

    /**
     * Constructor used for launching new intent when finished
     * @param ctx
     * @param intent
     * @param name - name of search type used to display on Activity Title
     */
    public AsyncDownloader(Context ctx, IntentItem intent, String name) {
        context = ctx;
        intentItem = intent;
        title = name;
    }

    /**
     * Constructor used for callback when finished
     *
     * @param listener
     */
    public AsyncDownloader(OnApiCallCompleted listener) {
        this.listener=listener;
    }

    /**
     * onPreExecute runs on the UI thread and before doInBackground
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //If context is passed show progress dialog
        if(context != null){
            dialog = new ProgressDialog(context);
            dialog.setMessage("Loading...");
            dialog.setProgressStyle(dialog.STYLE_SPINNER);
            dialog.setCancelable(false);
            dialog.show();
        }

    }

    @Override
    protected String doInBackground(String... params) {

        String url = params[0];

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request);

        Response response = null;

        String jsonData = null;

        try {
            response = call.execute();

            if (response.isSuccessful()) {
                jsonData = response.body().string();

            } else {
                jsonData = null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonData;
    }

    /**
     * onPostExecute runs on the  main thread
     * data will be sent via callback or new Intent
     *
     * @param jsonData
     */
    @Override
    protected void onPostExecute(String jsonData) {
        super.onPostExecute(jsonData);

        //If listener send callback otherwise execute intent
        if(listener != null){
            listener.onApiCallCompleted(jsonData);
        }else {
            Intent intent;
            switch (intentItem.getType()){
                case PLAY_VIDEO:
                    String key = getFirstVideoKey(jsonData);
                    if(key != null){
                        intent = intentItem.getIntent(key);
                        context.startActivity(intent);
                    }else{
                        Toast.makeText(context,"Video Does Not Exist",Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    intent = intentItem.getIntent(null);
                    intent.putExtra("jsonData", jsonData);
                    intent.putExtra("title",title);
                    intent.putExtra("type",intentItem.getType());
                    context.startActivity(intent);
                    break;
            }
            dialog.dismiss();
        }
    }

    /**
     * Used for launching YouTubePlayer with video key
     *
     * @param jsonData
     * @return
     */
    private String getFirstVideoKey(String jsonData) {
        JSONObject jsonResponse;
        try {
            jsonResponse = new JSONObject(jsonData);
            JSONArray results = jsonResponse.getJSONArray("results");
            if (results != null && results.length() > 0){
                JSONObject jsonVideo = results.getJSONObject(0);
                return jsonVideo.getString("key");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}