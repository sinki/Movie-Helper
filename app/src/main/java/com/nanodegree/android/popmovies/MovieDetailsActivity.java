package com.nanodegree.android.popmovies;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
** Activity to show details of a movie
 */
public class MovieDetailsActivity extends AppCompatActivity {

  private String LOG_TAG = this.getClass().getSimpleName();
  private RecyclerView movieTrailersView;
  private RecyclerView movieReviewsView;
  private MovieTrailersAdapter movieTrailersAdapter;
  private MovieReviewsAdapter movieReviewsAdapter;
  private SharedPreferences preferences;
  private ImageButton markFavoriteBtn;
  private Gson gson = new Gson();

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
    getDetailsForMovie(movie.getId());
    getTrailersForMovie(movie.getId());
    getReviewsForMovie(movie.getId());

    preferences = this.getSharedPreferences("pop_mvs_fvt", 0);
    toolbar.setTitle(movie.getTitle());
    setSupportActionBar(toolbar);
    //set back button to go back to the main page
    movieTrailersView = (RecyclerView) findViewById(R.id.trailer_list);
    movieReviewsView = (RecyclerView) findViewById(R.id.review_list);
    markFavoriteBtn = (ImageButton) findViewById(R.id.mark_favorite_btn);
    markFavoriteBtn.setTag(R.string.movie_id_tag, String.valueOf(movie.getId()));
    markFavoriteBtn.setTag(R.string.movie_poster_image_tag, movie.getPoster_path());

    Set<Movie> favoriteMovieList = new HashSet<Movie>();
    String favoriteMovieListJson = preferences.getString("fvt_mv_lst", null);
    if(favoriteMovieListJson != null){
      favoriteMovieList.addAll((List<Movie>) gson.fromJson(favoriteMovieListJson,
          new TypeToken<List<Movie>>() {
          }.getType()));
      for(Movie m : favoriteMovieList){
        if(m.getId() == movie.getId()){
          markFavoriteBtn.setImageResource(R.drawable.star);
        }
      }
    }

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

  @SuppressWarnings({"ConstantConditions", "unchecked"})
  public void markMovieAsFavorite(View view) {
    if (view.getId() == R.id.mark_favorite_btn) {
      Set<Movie> favoriteMovieList = new HashSet<Movie>();
      String favoriteMovieListJson = preferences.getString("fvt_mv_lst", null);
      if(favoriteMovieListJson != null){
        favoriteMovieList.addAll((List<Movie>) gson.fromJson(favoriteMovieListJson,
            new TypeToken<List<Movie>>() {
            }.getType()));
      }
      String movieIdString = (String) view.getTag(R.string.movie_id_tag);
      String movieIdPosterPath = (String)view.getTag(R.string.movie_poster_image_tag);
      Movie movie = new Movie();
      movie.setId(Long.valueOf(movieIdString));
      movie.setPoster_path(movieIdPosterPath);
      boolean isAlreadyAdded = false;
      for(Movie m : favoriteMovieList){
        if(m.getId() == Long.valueOf(movieIdString)){
          isAlreadyAdded = true;
        }
      }
      if (!isAlreadyAdded) {
        favoriteMovieList.add(movie);
        String json = gson.toJson(favoriteMovieList);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("fvt_mv_lst", json);
        editor.apply();
        markFavoriteBtn.setImageResource(R.drawable.star);
        Toast.makeText(getApplicationContext(), "Added to Favorites", Toast.LENGTH_SHORT).show();
      } else {
       /* SharedPreferences.Editor edit = preferences.edit();
        favoriteMovieList.remove(movieIdString);
        edit.putStringSet("fvt_mv_lst", favoriteMovieList);
        edit.apply();
        //  markFavoriteBtn.setText(R.string.mark_favorite_button);
        markFavoriteBtn.setImageResource(R.drawable.star_outline);
        Toast.makeText(getApplicationContext(), "Removed from Favorites", Toast.LENGTH_SHORT).show();*/
      }
    }
  }

  private void getDetailsForMovie(long movieId) {
    new GetMovieDetailsTask().execute(movieId);
  }

  public class GetMovieDetailsTask extends AsyncTask<Long, Void, Movie> {

    @Override
    protected Movie doInBackground(Long... params) {
      HttpURLConnection urlConnection;
      BufferedReader reader;
      String movieDetailResponseString;
      if (params.length == 0) {
        return null;
      }
      Long movieId = params[0];
      if (movieId == null) {
        return null;
      }
      try {
        URL url = new URL(getApiUrlForMovieDetails(movieId));
        Log.d(LOG_TAG, "details url - " + url);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();

        InputStream inputStream = urlConnection.getInputStream();
        StringBuilder buffer = new StringBuilder();
        if (inputStream == null) {
          return null;
        }
        reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
          buffer.append(line).append("\n");
        }
        if (buffer.length() == 0) {
          return null;
        }
        movieDetailResponseString = buffer.toString();
        Log.d(LOG_TAG, movieDetailResponseString);
        return getMovieDetailsFromResponse(movieDetailResponseString);
      } catch (IOException e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(Movie movieDetails) {
      fillMovieDataInView(movieDetails);
    }

    private Movie getMovieDetailsFromResponse(String movieJsonStr) {
      Movie response = null;
      if (movieJsonStr != null && movieJsonStr.length() > 0) {
        try {
          Gson gson = new Gson();
          response = gson.fromJson(movieJsonStr, Movie.class);
        } catch (IllegalStateException e) {
          e.printStackTrace();
        }
      }
      return response;
    }

    private String getApiUrlForMovieDetails(Long movieId) {
      String API_KEY = "api_key";
      Uri.Builder builder = Uri.parse(PopMoviesAppConstants.MOVIE_DB_BASE_URL
          .concat("movie/")
          .concat(movieId.toString())).buildUpon();
      builder.appendQueryParameter(API_KEY, PopMoviesAppConstants.MOVIE_DB_API_KEY);
      return builder.build().toString();
    }
  }

  private void getTrailersForMovie(long movieId) {
    new GetMovieTrailersTask().execute(movieId);
  }

  public class GetMovieTrailersTask extends AsyncTask<Long, Void, List<MovieTrailer>> {

    @Override
    protected List<MovieTrailer> doInBackground(Long... params) {
      HttpURLConnection urlConnection;
      BufferedReader reader;
      String trailerListResponseString;
      if (params.length == 0) {
        return null;
      }
      Long movieId = params[0];
      if (movieId == null) {
        return null;
      }
      try {
        URL url = new URL(getApiUrlForTrailerList(movieId));
        Log.d(LOG_TAG, "trailer url - " + url);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();

        InputStream inputStream = urlConnection.getInputStream();
        StringBuilder buffer = new StringBuilder();
        if (inputStream == null) {
          return null;
        }
        reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
          buffer.append(line).append("\n");
        }
        if (buffer.length() == 0) {
          return null;
        }
        trailerListResponseString = buffer.toString();
        Log.d(LOG_TAG, trailerListResponseString);
        return getTrailerListFromResponse(trailerListResponseString);
      } catch (IOException e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(List<MovieTrailer> movieTrailers) {
      if (movieTrailers != null && !movieTrailers.isEmpty()) {
        movieTrailersAdapter = new MovieTrailersAdapter(movieTrailers, getApplicationContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        movieTrailersView.setLayoutManager(mLayoutManager);
        movieTrailersView.setItemAnimator(new DefaultItemAnimator());
        movieTrailersView.setAdapter(movieTrailersAdapter);
      }
    }

    private List<MovieTrailer> getTrailerListFromResponse(String productJSONStr) {
      MovieTrailerListResponse response = null;
      if (productJSONStr != null && productJSONStr.length() > 0) {
        try {
          Gson gson = new Gson();
          response = gson.fromJson(productJSONStr, MovieTrailerListResponse.class);
        } catch (IllegalStateException e) {
          e.printStackTrace();
        }
      }
      if (response != null) {
        return response.getResults();
      } else {
        return new ArrayList<>();
      }
    }

    private String getApiUrlForTrailerList(Long movieId) {
      String API_KEY = "api_key";
      Uri.Builder builder = Uri.parse(PopMoviesAppConstants.MOVIE_DB_BASE_URL
          .concat("movie/")
          .concat(movieId.toString())
          .concat("/videos")).buildUpon();
      builder.appendQueryParameter(API_KEY, PopMoviesAppConstants.MOVIE_DB_API_KEY);
      return builder.build().toString();
    }
  }

  private void getReviewsForMovie(Long movieId) {
    new GetMovieReviewsTask().execute(movieId);
  }

  public class GetMovieReviewsTask extends AsyncTask<Long, Void, List<MovieReview>> {

    @Override
    protected List<MovieReview> doInBackground(Long... params) {
      HttpURLConnection urlConnection;
      BufferedReader reader;
      String reviewListResponseString;
      if (params.length == 0) {
        return null;
      }
      Long movieId = params[0];
      if (movieId == null) {
        return null;
      }
      try {
        URL url = new URL(getApiUrlForReviewList(movieId));
        Log.d(LOG_TAG, "review api url - " + url);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();

        InputStream inputStream = urlConnection.getInputStream();
        StringBuilder buffer = new StringBuilder();
        if (inputStream == null) {
          return null;
        }
        reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
          buffer.append(line).append("\n");
        }
        if (buffer.length() == 0) {
          return null;
        }
        reviewListResponseString = buffer.toString();
        Log.d(LOG_TAG, reviewListResponseString);
        return getReviewListFromResponse(reviewListResponseString);
      } catch (IOException e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(List<MovieReview> movieReviews) {
      if (movieReviews != null && !movieReviews.isEmpty()) {
        movieReviewsAdapter = new MovieReviewsAdapter(movieReviews, getApplicationContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        movieReviewsView.setLayoutManager(mLayoutManager);
        movieReviewsView.setItemAnimator(new DefaultItemAnimator());
        movieReviewsView.setAdapter(movieReviewsAdapter);
      }
    }

    private List<MovieReview> getReviewListFromResponse(String reviewJSONStr) {
      MovieReviewListResponse response = null;
      if (reviewJSONStr != null && reviewJSONStr.length() > 0) {
        try {
          Gson gson = new Gson();
          response = gson.fromJson(reviewJSONStr, MovieReviewListResponse.class);
        } catch (IllegalStateException e) {
          e.printStackTrace();
        }
      }
      if (response != null) {
        return response.getResults();
      } else {
        return new ArrayList<>();
      }
    }

    private String getApiUrlForReviewList(Long movieId) {
      String API_KEY = "api_key";
      Uri.Builder builder = Uri.parse(PopMoviesAppConstants.MOVIE_DB_BASE_URL
          .concat("movie/")
          .concat(movieId.toString())
          .concat("/reviews")).buildUpon();
      builder.appendQueryParameter(API_KEY, PopMoviesAppConstants.MOVIE_DB_API_KEY);
      return builder.build().toString();
    }
  }


}
