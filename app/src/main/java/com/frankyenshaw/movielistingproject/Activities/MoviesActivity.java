package com.frankyenshaw.movielistingproject.Activities;

import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;

import com.frankyenshaw.movielistingproject.Controllers.Listeners.EndlessRecyclerViewScrollListener;
import com.frankyenshaw.movielistingproject.Controllers.Listeners.ItemClickListener;
import com.frankyenshaw.movielistingproject.Controllers.Listeners.OnApiCallCompleted;
import com.frankyenshaw.movielistingproject.Controllers.Adapters.MovieAdapter;
import com.frankyenshaw.movielistingproject.Controllers.Http.MovieDbApi;
import com.frankyenshaw.movielistingproject.Models.IntentItem;
import com.frankyenshaw.movielistingproject.Models.Movie;
import com.frankyenshaw.movielistingproject.R;
import com.google.android.youtube.player.YouTubeIntents;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by frankyenshaw on 12/29/16.
 */

public class MoviesActivity extends AppCompatActivity
        implements ItemClickListener {

    private static final String TAG = MoviesActivity.class.getSimpleName();

    private EndlessRecyclerViewScrollListener scrollListener;
    private OrientationEventListener mOrientationListener;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private Context context;
    private ArrayList<Movie> movies;
    private String title;
    private IntentItem.IntentType type;
    private final String KEY_RECYCLER_STATE = "recycler_state";
    private final String LAST_PAGE_STATE = "last_page_state";
    private final String MOVIES_STATE = "movies_state";


    private RecyclerView mRecyclerView;
    private int lastPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);
        mRecyclerView = (RecyclerView) findViewById(R.id.moviesRecyclerView);
        context = this;
        Intent intent = getIntent();
        if (savedInstanceState != null) {
            // Restore value from saved state
            movies = savedInstanceState.getParcelableArrayList(MOVIES_STATE);
            lastPage = savedInstanceState.getInt(LAST_PAGE_STATE);
            Parcelable listState = savedInstanceState.getParcelable(KEY_RECYCLER_STATE);
            populateRecyclerView();
            mRecyclerView.getLayoutManager().onRestoreInstanceState(listState);
            scrollListener.restoreState(lastPage,movies.size());
        } else {
            if (intent != null) {
                String jsonData = intent.getExtras().getString("jsonData");
                getMovies(jsonData);
                populateRecyclerView();
            }
        }

        if (intent != null) {
            title = intent.getExtras().getString("title");
            type = (IntentItem.IntentType) intent.getSerializableExtra("type");
            setTitle(title);
        }

        //Handle Orientation Change and populate Recycler View to display new layout
        mOrientationListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {
                Log.d(TAG, "Orientation changed to " + orientation);
                populateRecyclerView();
            }
        };

        //Handle SwipeRefresh  and populate Recycler View to display new results
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MovieDbApi movieDbApi = MovieDbApi.getInstance();
                movieDbApi.getMoviesWithCallback(mOnApiCallRefreshCompleted,type,1);
            }
        });


    }

    /**
     * Save movie list and RecyclerView State for better orientation change UX
     *
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIES_STATE, movies);
        outState.putInt(LAST_PAGE_STATE,lastPage);
        Parcelable listState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(KEY_RECYCLER_STATE, listState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mOrientationListener.disable();
    }

    @Override
    public void onClick(View view, int position) {
        Movie clickedMovie = movies.get(position);
        Log.d(TAG,"Clicked Position: "+Integer.toString(position));
        Log.d(TAG, "Movie: "+clickedMovie.toString());
        Log.d(TAG, "Movie Popular Vote: "+clickedMovie.getVoteAverage());

        MovieDbApi movieDbApi = MovieDbApi.getInstance();
        if(clickedMovie.isPopular()){
            IntentItem intent = new IntentItem(this, IntentItem.IntentType.PLAY_VIDEO);
            movieDbApi.getVideoAndPlayWithIntent(this, intent,clickedMovie.getId());
        } else {
            Intent i = new Intent(context, new MovieDetailsActivity().getClass());
            i.putExtra("movie", clickedMovie);
            context.startActivity(i);

            //Launch Movie Details Page and pull additional Videos from API
//            IntentItem intent = new IntentItem(this, new MovieDetailsActivity().getClass(), IntentItem.IntentType.GET_VIDEOS,clickedMovie.getTitle());
//            movieDbApi.getVideosWithIntent(this, intent);
        }
    }


    private OnApiCallCompleted mOnApiCallRefreshCompleted = new OnApiCallCompleted() {
        @Override
        public void onApiCallCompleted(String jsonData) {
            //Refreshing so reset movie list,scroll listener,paging
            movies.clear();
            scrollListener.resetState();
            lastPage = 1;
            getMovies(jsonData);
            populateRecyclerView();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    };
    private OnApiCallCompleted mOnApiCallInfiniteScrollCompleted = new OnApiCallCompleted() {
        @Override
        public void onApiCallCompleted(String jsonData) {
            appendToRecyclerView(jsonData);
            mSwipeRefreshLayout.setRefreshing(false);
        }
    };

    private void populateRecyclerView() {
        MovieAdapter adapter = new MovieAdapter(MoviesActivity.this, movies);
        adapter.setClickListener(this);
        mRecyclerView.setAdapter(adapter);


        LinearLayoutManager layoutManager = new LinearLayoutManager(MoviesActivity.this);
        mRecyclerView.setLayoutManager(layoutManager);



        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener((LinearLayoutManager)mRecyclerView.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                lastPage = page;
                MovieDbApi movieDbApi = MovieDbApi.getInstance();
                movieDbApi.getMoviesWithCallback(mOnApiCallInfiniteScrollCompleted,type,page);
            }
        };
        // Adds the scroll listener to RecyclerView
        mRecyclerView.addOnScrollListener(scrollListener);
    }

    /**
     * Append results when using infinite scroll
     *
     * @param jsonData
     */
    private void appendToRecyclerView(String jsonData){
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.moviesRecyclerView);
        int currentSize = movies.size();
        getMovies(jsonData);
        int newSize = movies.size();
        MovieAdapter adapter = (MovieAdapter) recyclerView.getAdapter();
        adapter.notifyItemRangeInserted(currentSize-1,newSize-currentSize);


    }


    private void getMovies(String jsonData) {

        JSONObject jsonResponse = null;
        try {
            jsonResponse = new JSONObject(jsonData);

            JSONArray results = jsonResponse.getJSONArray("results");

            int dataSize = results.length();

            if(movies == null) {
                movies = new ArrayList<>(dataSize);
            }

            for (int i = 0; i < dataSize; i++) {

                JSONObject jsonMovie = results.getJSONObject(i);

                Movie movie = new Movie();

                movie.setId(jsonMovie.getInt("id"));
                movie.setTitle(jsonMovie.getString("title"));
                movie.setPosterPath(jsonMovie.getString("poster_path"));
                movie.setBackdropPath(jsonMovie.getString("backdrop_path"));
                movie.setOverview(jsonMovie.getString("overview"));
                movie.setVoteAverage((float) jsonMovie.getDouble("vote_average"));
                movie.setPopularity((float) jsonMovie.getDouble("popularity"));

                movies.add(movie);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

