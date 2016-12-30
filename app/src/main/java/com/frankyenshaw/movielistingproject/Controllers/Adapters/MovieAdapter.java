package com.frankyenshaw.movielistingproject.Controllers.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.frankyenshaw.movielistingproject.Controllers.Listeners.ItemClickListener;
import com.frankyenshaw.movielistingproject.Controllers.Transformers.RoundedTransform;
import com.frankyenshaw.movielistingproject.Models.Movie;
import com.frankyenshaw.movielistingproject.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by frankyenshaw on 12/29/16.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private List<Movie> movies;
    private Context context;
    private ItemClickListener clickListener;

    public MovieAdapter(Context ctx, List<Movie> moviesArray) {
        context = ctx;
        movies = moviesArray;

    }

    //This method inflates a layout from XML and returning the holder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Inflate the custom layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_item, parent, false);

        return new ViewHolder(view);
    }

    // Populates data into the item through holder
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Movie movie = movies.get(position);

        holder.movieTitle.setText(movie.getTitle());
        holder.movieTitle.setTextSize(context.getResources().getDimension(R.dimen.textsize));
        holder.movieOverview.setText(movie.getOverview());

        if (movie.isPopular()) {
            holder.playButtonImage.setVisibility(View.VISIBLE);
        } else {
            holder.playButtonImage.setVisibility(View.INVISIBLE);
        }
        if (holder.posterImage != null) {
            Picasso.with(context)
                    .load(movie.getPosterPath())
                    .placeholder(R.drawable.loading)
                    .transform(new RoundedTransform(20, 2))
                    .error(R.drawable.no_poster)
                    .into(holder.posterImage);
        }
        if (holder.backdropImage != null) {
            Picasso.with(context)
                    .load(movie.getBackdropPath())
                    .placeholder(R.drawable.loading)
                    .transform(new RoundedTransform(20, 2))
                    .error(R.drawable.no_poster)
                    .into(holder.backdropImage);
        }
    }

    @Override
    public int getItemCount() {
        if (movies == null) {
            return 0;
        }
        return movies.size();
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }


    /**
     * Provides a direct reference to each of the views within a data item
     * Used to cache the views within the item layout for fast access
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView movieTitle;
        public TextView movieOverview;
        public ImageView posterImage;
        public ImageView backdropImage;
        public ImageView playButtonImage;


        public ViewHolder(View itemView) {
            super(itemView);
            movieTitle = (TextView) itemView.findViewById(R.id.movieTitle);
            movieOverview = (TextView) itemView.findViewById(R.id.movieOverview);
            posterImage = (ImageView) itemView.findViewById(R.id.moviePosterImage);
            backdropImage = (ImageView) itemView.findViewById(R.id.movieBackdropImage);
            playButtonImage = (ImageView) itemView.findViewById(R.id.playButtonImage);

            itemView.setTag(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onClick(view, getAdapterPosition());
        }
    }
}
