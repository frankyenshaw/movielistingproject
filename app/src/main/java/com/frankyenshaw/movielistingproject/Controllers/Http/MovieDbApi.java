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

    private static final String TAG = MovieDbApi.class.getSimpleName();
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
    public String getTopRatedUrl(){
        return url + "movie/top_rated?api_key=" + API_KEY;
    }
    public String getUpcomingUrl(){ return url + "movie/upcoming?api_key=" + API_KEY; }
    public String getVideosUrl(int movieId){ return url + "movie/"+movieId+"/videos?api_key=" + API_KEY; }
    public String getSearchUrl(String query){ return url + "search/movie/videos?query="+query+"&api_key=" + API_KEY; }

    public String getEndpointUrl(IntentItem.IntentType type){
        String endpoint;
        switch (type){
            case POPULAR_MOVIES:
                endpoint = this.getPopularUrl();
                break;
            case TOP_RATED:
                endpoint = this.getTopRatedUrl();
                break;
            case NOW_PLAYING_MOVIES:
                endpoint = this.getNowPlayingUrl();
                break;
            case UPCOMING_MOVIES:
                endpoint = this.getUpcomingUrl();
                break;
            default:
                endpoint = url;
        }
        return endpoint;
    }

    /**
     * Used to retrieve data from movie endpoint and luanches new activity with RecyclerView
     *
     * @param context
     * @param intent
     */
    public void getMoviesWithIntent(Context context, IntentItem intent){
        String endpoint = this.getEndpointUrl(intent.getType());
        AsyncDownloader downloader = new AsyncDownloader(context, intent, intent.getTitle());
        downloader.execute(endpoint);
    }

    /**
     * Not Used ATM
     * Used to retrieve data from movie endpoint and luanches new activity with RecyclerView
     *
     * @param context
     * @param intent
     */
    public void searchMoviesWithIntent(Context context, IntentItem intent,String query){
        String endpoint = getSearchUrl(query);
        AsyncDownloader downloader = new AsyncDownloader(context, intent, intent.getTitle());
        downloader.execute(endpoint);
    }

    /**
     * Used to retreive data from movie endpoint with paging options and returns results via callback
     * Currently used for infinite scroll
     *
     * @param callback
     * @param type
     * @param page
     */
    public void getMoviesWithCallback(OnApiCallCompleted callback, IntentItem.IntentType type,int page){
        String endpoint = this.getEndpointUrl(type);
        endpoint = appendPageAttribute(endpoint,page);
        AsyncDownloader downloader = new AsyncDownloader(callback);
        downloader.execute(endpoint);
    }

    /**
     * Not used ATM but similar to getMoviesWithIntent
     * Used to retreive video information for a specific movie and launches new activity
     *
     * @param context
     * @param intent
     */
    public void getVideosWithIntent(Context context, IntentItem intent,int movieId){
        String endpoint = getVideosUrl(movieId);
        AsyncDownloader downloader = new AsyncDownloader(context, intent, intent.getTitle());
        downloader.execute(endpoint);
    }

    /**
     * Hits videos endpoint for moveId and plays first video with YouTube API in new window
     *
     * @param context
     * @param intent
     * @param movieId
     */
    public void getVideoAndPlayWithIntent(Context context, IntentItem intent,int movieId){
        String endpoint = getVideosUrl(movieId);
        AsyncDownloader downloader = new AsyncDownloader(context, intent, intent.getTitle());
        downloader.execute(endpoint);
    }


    private String appendPageAttribute(String endpoint, int page){
        StringBuilder sb = new StringBuilder();
        sb.append(endpoint);
        if(page > 1){
            sb.append("&page="+page);
            Log.d(TAG,"Page appended URL: "+endpoint);
        }
        return sb.toString();
    }

}
