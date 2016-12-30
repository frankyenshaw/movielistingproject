package com.frankyenshaw.movielistingproject.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.frankyenshaw.movielistingproject.Controllers.Http.MovieDbApi;
import com.frankyenshaw.movielistingproject.Models.IntentItem;
import com.frankyenshaw.movielistingproject.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    private MovieDbApi movieDbApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button nowPlayingButton = (Button) findViewById(R.id.now_playing_button);
        nowPlayingButton.setOnClickListener(this);
        final Button popularButton = (Button) findViewById(R.id.popular_button);
        popularButton.setOnClickListener(this);
        final Button upcomingButton = (Button) findViewById(R.id.upcoming_button);
        upcomingButton.setOnClickListener(this);
        final Button topRatedButton = (Button) findViewById(R.id.top_rated_button);
        topRatedButton.setOnClickListener(this);

        this.movieDbApi = MovieDbApi.getInstance();
    }

    @Override
    public void onClick(View v) {
        IntentItem intent;
        switch (v.getId()) {
            case R.id.now_playing_button:
                intent = new IntentItem(this, new MoviesActivity().getClass(), IntentItem.IntentType.NOW_PLAYING_MOVIES, "Now Playing");
                this.movieDbApi.getMoviesWithIntent(this, intent);
                break;
            case R.id.popular_button:
                intent = new IntentItem(this, new MoviesActivity().getClass(), IntentItem.IntentType.POPULAR_MOVIES, "Popular Movies");
                this.movieDbApi.getMoviesWithIntent(this, intent);
                break;
            case R.id.upcoming_button:
                intent = new IntentItem(this, new MoviesActivity().getClass(), IntentItem.IntentType.UPCOMING_MOVIES, "Upcoming Movies");
                this.movieDbApi.getMoviesWithIntent(this, intent);
                break;
            case R.id.top_rated_button:
                intent = new IntentItem(this, new MoviesActivity().getClass(), IntentItem.IntentType.TOP_RATED, "Top Rated Movies");
                this.movieDbApi.getMoviesWithIntent(this, intent);
                break;
            default:
                Log.w(TAG, "Unknown Button Click");
        }

    }
}

