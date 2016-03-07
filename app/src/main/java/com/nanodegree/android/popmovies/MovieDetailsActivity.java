package com.nanodegree.android.popmovies;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class MovieDetailsActivity extends AppCompatActivity {

  private String LOG_TAG = this.getClass().getSimpleName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_movie_details);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    Bundle bundle = getIntent().getExtras();
    Movie movie = bundle.getParcelable("movie_obj");
    if (movie == null) {
      Toast.makeText(this, "No movie details found", Toast.LENGTH_SHORT).show();
      finish();
      return;
    }
    toolbar.setTitle(movie.getTitle());
    setSupportActionBar(toolbar);
    //set back button to go back to the main page
    final ActionBar supportAB = getSupportActionBar();
    if (supportAB != null) {
      supportAB.setHomeAsUpIndicator(R.drawable.back_button);
      supportAB.setDisplayHomeAsUpEnabled(true);
      supportAB.setDisplayShowHomeEnabled(true);
      supportAB.setDisplayShowTitleEnabled(true);
    }
    toolbar.setNavigationIcon(R.drawable.back_button);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });
    fillMovieDataInView(movie);
  }

  private void fillMovieDataInView(Movie movie) {
    TextView movieTitle = (TextView) findViewById(R.id.movie_title);
    TextView movieReleaseDate = (TextView) findViewById(R.id.movie_release_date);
    TextView movieRating = (TextView) findViewById(R.id.movie_rating);
    TextView movieOverview = (TextView) findViewById(R.id.movie_overview);
    ImageView moviePosterImage = (ImageView) findViewById(R.id.movie_thumbnail);

    String posterPath = movie.getPoster_path();
    if (posterPath == null) {
      Log.i(LOG_TAG, "Poster path null for " + movie.getTitle());
      //default
      moviePosterImage.setBackgroundResource(R.color.grey);
    } else {
      String posterImageUrl = ImageUrlHelper.getPicassoImageUrl(posterPath, PopMoviesAppConstants.PICASSO_THUMBNAIL_SIZE_BIG);
      Picasso.with(getApplicationContext()).load(posterImageUrl).into(moviePosterImage);
    }
    movieTitle.setText(movie.getOriginal_title());
    movieOverview.setText(movie.getOverview());
    movieRating.setText(String.valueOf(movie.getVote_average()).concat("/10"));
    movieReleaseDate.setText(movie.getRelease_date());
  }

}
