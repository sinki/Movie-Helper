package com.nanodegree.android.popmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nanodegree.android.popmovies.R;
import com.nanodegree.android.popmovies.domain.MovieTrailer;

import java.util.List;

/**
 * Created by sinki.agarwal on 4/24/2016.
 */
public class MovieTrailersAdapter extends RecyclerView.Adapter<MovieTrailersAdapter.MovieTrailerHolder>{

  private List<MovieTrailer> movieTrailerList;
  private Context ctx;

  public MovieTrailersAdapter(List<MovieTrailer> movieTrailerList, Context ctx) {
    this.movieTrailerList = movieTrailerList;
    this.ctx = ctx;
  }

  @Override
  public MovieTrailerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.movie_trailer_row, parent, false);

    return new MovieTrailerHolder(itemView);
  }

  @Override
  public void onBindViewHolder(MovieTrailerHolder holder, int position) {
    final MovieTrailer movieTrailer = movieTrailerList.get(position);
    holder.trailerName.setText(movieTrailer.getName());
    holder.trailerPlayBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if(movieTrailer.getKey() != null && !movieTrailer.getKey().isEmpty()
            && "Youtube".equalsIgnoreCase(movieTrailer.getSite())){
          String youtubeUrl = "http://www.youtube.com/watch?v=" + movieTrailer.getKey();
          Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
          intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          ctx.startActivity(intent);
        } else {
          Toast.makeText(ctx, "Video not available", Toast.LENGTH_SHORT).show();
        }
      }
    });

  }

  @Override
  public int getItemCount() {
    return movieTrailerList.size();
  }

  public static class MovieTrailerHolder extends RecyclerView.ViewHolder {

       LinearLayout trailerView;
       TextView trailerName;
       ImageButton trailerPlayBtn;

       public MovieTrailerHolder(View itemView) {
         super(itemView);
         trailerView = (LinearLayout) itemView.findViewById(R.id.trailer_row_view);
         trailerName = (TextView) itemView.findViewById(R.id.trailer_name);
         trailerPlayBtn = (ImageButton) itemView.findViewById(R.id.trailer_play_btn);
       }
     }
}
