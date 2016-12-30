package com.frankyenshaw.movielistingproject.Controllers.Http;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.frankyenshaw.movielistingproject.Controllers.Listeners.OnApiCallCompleted;
import com.frankyenshaw.movielistingproject.Models.IntentItem;

/**
 * Created by frankyenshaw on 12/29/16.
 */

public class MovieDbApi {

    private volatile static MovieDbApi uniqueInstance;
    private final String url = "http://api.themoviedb.org/3/";
    private final String API_KEY = "a07e22bc18f5cb106bfe4cc1f83ad8ed";

    /**
     * Get unique instance of MovieDbApi
     *
     * @return
     */
    public static MovieDbApi getInstance() {
        if (uniqueInstance == null) {
            synchronized (MovieDbApi.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new MovieDbApi();
                }
            }
        }
        return uniqueInstance;
    }

    public String getNowPlayingUrl(){
        return url + "movie/now_playing?api_key=" + API_KEY;
    }
    public String getPopularUrl(){
        return url + "movie/popular?api_key=" + API_KEY;
    }
    public String getUpcomingUrl(){ return url + "movie/upcoming?api_key=" + API_KEY; }
    public String getVideosUrl(int movieId){ return url + "movie/"+movieId+"/videos?api_key=" + API_KEY; }

    public String getEndpointUrl(IntentItem.IntentType type){
        String endpoint;
        switch (type){
            case POPULAR_MOVIES:
                endpoint = this.getPopularUrl();
                break;
            case NOW_PLAYING_MOVIES:
                endpoint = this.getNowPlayingUrl();
                break;
            case UPCOMING_MOVIES:
                endpoint = this.getNowPlayingUrl();
                break;
            default:
                endpoint = url;
        }
        return endpoint;
    }

    /**
     * Helper function used to
     * @param context
     * @param intent
     */
    public void getMoviesWithIntent(Context context, IntentItem intent){
        String endpoint = this.getEndpointUrl(intent.getType());
        AsyncDownloader downloader = new AsyncDownloader(context, intent, intent.getTitle());
        downloader.execute(endpoint);
    }

    public void getMoviesWithCallback(OnApiCallCompleted callback, IntentItem.IntentType type,int page){
        String endpoint = this.getEndpointUrl(type);
        if(page > 1){
            StringBuilder sb = new StringBuilder();
            sb.append(endpoint);
            sb.append("&page="+page);
            endpoint = sb.toString();
        }
        Log.d("TEST","URL: "+endpoint);
        AsyncDownloader downloader = new AsyncDownloader(callback);
        downloader.execute(endpoint);
    }

    public void getVideosWithIntent(Context context, IntentItem intent){
        String endpoint = this.getEndpointUrl(intent.getType());
        AsyncDownloader downloader = new AsyncDownloader(context, intent, intent.getTitle());
        downloader.execute(endpoint);
    }
    public void getVideoAndPlayWithIntent(Context context, IntentItem intent,int movieId){
        String endpoint = getVideosUrl(movieId);
        AsyncDownloader downloader = new AsyncDownloader(context, intent, intent.getTitle());
        downloader.execute(endpoint);
    }



}
