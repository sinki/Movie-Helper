package com.nanodegree.android.popmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by sinki.agarwal on 4/24/2016.
 */
public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.MovieReviewHolder> {

  private List<MovieReview> movieReviewList;
  private Context ctx;

  public MovieReviewsAdapter(List<MovieReview> movieTrailerList, Context ctx) {
    this.movieReviewList = movieTrailerList;
    this.ctx = ctx;
  }

  @Override
  public MovieReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.movie_review_row, parent, false);

    return new MovieReviewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(MovieReviewHolder holder, int position) {
    final MovieReview movieReview = movieReviewList.get(position);
    holder.reviewAuthor.setText(movieReview.getAuthor());
    holder.reviewContent.setText(movieReview.getContent());
  }

  @Override
  public int getItemCount() {
    return movieReviewList.size();
  }

  public static class MovieReviewHolder extends RecyclerView.ViewHolder {
    TextView reviewAuthor;
    TextView reviewContent;

    public MovieReviewHolder(View itemView) {
      super(itemView);
      reviewAuthor = (TextView) itemView.findViewById(R.id.review_author);
      reviewContent = (TextView) itemView.findViewById(R.id.review_content);
    }
  }
}
