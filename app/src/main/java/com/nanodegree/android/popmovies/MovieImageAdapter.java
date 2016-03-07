package com.nanodegree.android.popmovies;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;


public class MovieImageAdapter extends BaseAdapter {

  private String LOG_TAG = this.getClass().getSimpleName();

  private Context ctx;
  private List<Movie> movieList;

  public MovieImageAdapter(Context ctx, List<Movie> movies) {
    this.ctx = ctx;
    this.movieList = movies;
  }

  public void setMovieList(List<Movie> movieList) {
    this.movieList = movieList;
  }

  @Override
  public int getCount() {
    return movieList.size();
  }

  @Override
  public Object getItem(int i) {
    return movieList.get(i);
  }

  @Override
  public long getItemId(int i) {
    return 0;
  }

  @Override
  public View getView(int i, View convertView, ViewGroup viewGroup) {
    ImageView imageView;
    if (convertView == null) {
      // if it's not recycled, initialize some attributes
      imageView = new ImageView(ctx);
      imageView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.WRAP_CONTENT, GridView.LayoutParams.WRAP_CONTENT));
      imageView.setAdjustViewBounds(true);
      imageView.setPadding(0, 0, 0, 0);
    } else {
      imageView = (ImageView) convertView;
    }
    String posterPath = movieList.get(i).getPoster_path();
    if (posterPath == null) {
      Log.i(LOG_TAG, "poster path null for " + movieList.get(i).getTitle());
      return imageView;
    }
    String posterImageUrl = ImageUrlHelper.getPicassoImageUrl(posterPath, PopMoviesAppConstants.PICASSO_THUMBNAIL_SIZE_SMALL);
    // load image in view with the help of Picasso
    Picasso.with(ctx).load(posterImageUrl).into(imageView);
    final Movie movieObj = movieList.get(i);
    imageView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(ctx, MovieDetailsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("movie_obj", movieObj);
        ctx.startActivity(intent);
      }
    });
    return imageView;
  }
}
