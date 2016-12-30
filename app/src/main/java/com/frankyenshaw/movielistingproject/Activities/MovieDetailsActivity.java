package com.frankyenshaw.movielistingproject.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.frankyenshaw.movielistingproject.Controllers.Http.MovieDbApi;
import com.frankyenshaw.movielistingproject.Controllers.Transformers.RoundedTransform;
import com.frankyenshaw.movielistingproject.Models.IntentItem;
import com.frankyenshaw.movielistingproject.Models.Movie;
import com.frankyenshaw.movielistingproject.R;
import com.squareup.picasso.Picasso;

public class MovieDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private Movie movie;
    public static final String TAG = MovieDetailsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ImageView movieImage = (ImageView) findViewById(R.id.movieImage);
        TextView movieOverview = (TextView) findViewById(R.id.movieOverview);
        TextView ratingText = (TextView) findViewById(R.id.ratingText);
        TextView popularityText = (TextView) findViewById(R.id.popularityText);


        Intent intent = getIntent();
        if (intent != null) {
            //Currently sending parcelable Movie Object with Intent to avoid additional Api Calls
            //There are many other options that could be used such as caching, sqlite, local storage
            movie = intent.getParcelableExtra("movie");
            setTitle(movie.getTitle());
            ratingBar.setRating(movie.getVoteAverage());
            ratingText.setText(movie.getVoteAverage() + " / 10");
            popularityText.setText("Popularity: " + Math.round(movie.getPopularity()));
            Picasso.with(this)
                    .load(movie.getPosterPath())
                    .placeholder(R.drawable.loading)
                    .transform(new RoundedTransform(20, 2))
                    .error(R.drawable.no_poster)
                    .into(movieImage);
            movieOverview.setText(movie.getOverview());
        }


        movieImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        MovieDbApi movieDbApi = MovieDbApi.getInstance();
        IntentItem intent = new IntentItem(this, IntentItem.IntentType.PLAY_VIDEO);
        movieDbApi.getVideoAndPlayWithIntent(this, intent, movie.getId());
//        movieDbApi.getVideosWithIntent(this, intent);
    }
}
