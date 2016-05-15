package com.nanodegree.android.popmovies.notUsedAnyMore;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nanodegree.android.popmovies.domain.Movie;
import com.nanodegree.android.popmovies.adapters.MovieImageAdapter;
import com.nanodegree.android.popmovies.domain.MovieListResponse;
import com.nanodegree.android.popmovies.utils.MovieSortTypeEnum;
import com.nanodegree.android.popmovies.R;
import com.nanodegree.android.popmovies.utils.PopMovieAppConstants;

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
** Launcher activity to present grid view of movies. Default sort type is by Popularity.
 */
public class MovieListActivity extends AppCompatActivity {

  private int sortType = MovieSortTypeEnum.SORT_BY_POPULARITY.getId();
  private List<Movie> popularMovieList, topRatedMovieList;
  private GridView movieGridView;
  private MovieImageAdapter movieImageAdapter;
  private Gson gson = new Gson();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_movie_list);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    movieGridView = (GridView) findViewById(R.id.movie_grid);
    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    if(cm.getActiveNetworkInfo() == null){
      Toast.makeText(this, "No network found. Kindly connect to the internet first to use this app!", Toast.LENGTH_SHORT).show();
    } else {
      new GetMovieListTask().execute(getApiUrlForList());
    }
    int orientation = getResources().getConfiguration().orientation;
    setGridColumnsBasedOnOrientation(orientation);
  }

  private void setGridColumnsBasedOnOrientation(int orientation) {
    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
      movieGridView.setNumColumns(3);
    } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
      movieGridView.setNumColumns(5);
    }
  }

  private String getApiUrlForList() {
    String SORTING_PARAM = "sort_by";
    String API_KEY = "api_key";
    MovieSortTypeEnum movieSortTypeById = MovieSortTypeEnum.getMovieSortTypeById(sortType);
    Uri.Builder builder = Uri.parse(PopMovieAppConstants.MOVIE_DB_BASE_URL.concat("discover/movie")).buildUpon();
    builder.appendQueryParameter(SORTING_PARAM, movieSortTypeById.getApiParamName());
    builder.appendQueryParameter(API_KEY, PopMovieAppConstants.MOVIE_DB_API_KEY);
    if (sortType == MovieSortTypeEnum.SORT_BY_RATING.getId()) {
      //when getting movies sorted by rating, filter movies with considerable # of votes to get relevant data
      builder.appendQueryParameter("vote_count.gte", "100");
    }
    return builder.build().toString();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_movie_list, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id == R.id.action_sort_by_rating) {
      if (topRatedMovieList == null) {
        sortType = 2;
        new GetMovieListTask().execute(getApiUrlForList());
      } else if (movieImageAdapter == null) {
        new GetMovieListTask().execute(getApiUrlForList());
      } else {
        movieImageAdapter.setMovieList(topRatedMovieList);
        movieImageAdapter.notifyDataSetChanged();
      }
      return true;
    } else if (id == R.id.action_sort_popular) {
      sortType = 1;
      if (popularMovieList == null) {
        new GetMovieListTask().execute(getApiUrlForList());
      } else if (movieImageAdapter == null) {
        new GetMovieListTask().execute(getApiUrlForList());
      } else {
        movieImageAdapter.setMovieList(popularMovieList);
        movieImageAdapter.notifyDataSetChanged();
      }
    } else if(id == R.id.action_favorites){
      //show list of all favorite movies
      SharedPreferences preferences = this.getSharedPreferences("pop_mvs_fvt", 0);
      Set<Movie> favoriteMovieList = new HashSet<Movie>();
      String favoriteMovieListJson = preferences.getString("fvt_mv_lst", null);
      if(favoriteMovieListJson != null){
        favoriteMovieList.addAll((List<Movie>) gson.fromJson(favoriteMovieListJson,
            new TypeToken<List<Movie>>() {
            }.getType()));
        List<Movie> movieList = new ArrayList<Movie>(favoriteMovieList);
        if (movieImageAdapter == null) {
          movieImageAdapter = new MovieImageAdapter(getApplicationContext(), movieList);
          movieGridView.setAdapter(movieImageAdapter);
        } else {
          movieImageAdapter = new MovieImageAdapter(getApplicationContext(), movieList);
          movieGridView.setAdapter(movieImageAdapter);
          movieImageAdapter.setMovieList(movieList);
          movieImageAdapter.notifyDataSetChanged();
        }
      }

    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    setGridColumnsBasedOnOrientation(newConfig.orientation);
  }

  public class GetMovieListTask extends AsyncTask<String, Void, List<Movie>> {

    private final String LOG_TAG = this.getClass().getSimpleName();

    @Override
    protected List<Movie> doInBackground(String... params) {
      HttpURLConnection urlConnection;
      BufferedReader reader;
      String movieListResponseString;
      if (params.length == 0) {
        return null;
      }
      String movieListApiUrl = params[0];
      Log.i(LOG_TAG, movieListApiUrl);
      try {
        URL url = new URL(movieListApiUrl);
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
        movieListResponseString = buffer.toString();
        return getMovieListFromResponse(movieListResponseString);

      } catch (IOException e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(List<Movie> movieList) {
      if(movieList != null && !movieList.isEmpty()){
        if (sortType == MovieSortTypeEnum.SORT_BY_RATING.getId()) {
          topRatedMovieList = movieList;
        } else if (sortType == MovieSortTypeEnum.SORT_BY_POPULARITY.getId()) {
          popularMovieList = movieList;
        }
        if (movieImageAdapter == null) {
          movieImageAdapter = new MovieImageAdapter(getApplicationContext(), movieList);
          movieGridView.setAdapter(movieImageAdapter);
        } else {
          movieImageAdapter.setMovieList(movieList);
          movieImageAdapter.notifyDataSetChanged();
        }
      }
    }

    private List<Movie> getMovieListFromResponse(String productJSONStr) {
      MovieListResponse response = null;
      if (productJSONStr != null && productJSONStr.length() > 0) {
        try {
          Gson gson = new Gson();
          response = gson.fromJson(productJSONStr, MovieListResponse.class);
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
  }
}
