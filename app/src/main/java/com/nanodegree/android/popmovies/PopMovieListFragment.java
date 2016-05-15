package com.nanodegree.android.popmovies;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nanodegree.android.popmovies.adapters.MovieImageAdapter;
import com.nanodegree.android.popmovies.domain.Movie;
import com.nanodegree.android.popmovies.domain.MovieListResponse;
import com.nanodegree.android.popmovies.utils.MovieSortTypeEnum;
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


public class PopMovieListFragment extends Fragment {

  private static final String STATE_ACTIVATED_POSITION = "activated_position";
  private int mActivatedPosition = ListView.INVALID_POSITION;

  private int sortType = MovieSortTypeEnum.SORT_BY_POPULARITY.getId();
  private ArrayList<Movie> popularMovieList, topRatedMovieList;
  private GridView movieGridView;
  private MovieImageAdapter movieImageAdapter;
  private Gson gson = new Gson();


  public interface Callbacks {
    void onItemSelected(Movie movie);
  }

  public PopMovieListFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_popmovie_list, container, false);
    if (savedInstanceState != null) {
      sortType = savedInstanceState.getInt("sortType");
      topRatedMovieList = savedInstanceState.getParcelableArrayList("popMoviesList");
      popularMovieList = savedInstanceState.getParcelableArrayList("topRatedMoviesList");
    }

    movieGridView = (GridView) rootView.findViewById(R.id.movie_grid);
    movieGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Movie movie = (Movie) movieImageAdapter.getItem(i);
        ((Callbacks) getActivity()).onItemSelected(movie);
        mActivatedPosition = i;
      }
    });

    return rootView;
  }

  @Override
  public void onStart() {
    super.onStart();
    ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
    if (cm.getActiveNetworkInfo() == null) {
      Toast.makeText(getActivity(), "No network found. Kindly connect to the internet first to use this app!", Toast.LENGTH_SHORT).show();
    } else {
      if (sortType == 3) {
        getFavoritesMovies();
      } else if ((sortType == MovieSortTypeEnum.SORT_BY_POPULARITY.getId() && popularMovieList == null || popularMovieList.isEmpty())
          || (sortType == MovieSortTypeEnum.SORT_BY_RATING.getId() && topRatedMovieList == null || topRatedMovieList.isEmpty())) {
        new GetMovieListTask().execute(getApiUrlForList());
      }
    }
    int orientation = getResources().getConfiguration().orientation;
    setGridColumnsBasedOnOrientation(orientation);
  }

  private void getFavoritesMovies() {
    SharedPreferences preferences = getActivity().getSharedPreferences("pop_mvs_fvt", 0);
    Set<Movie> favoriteMovieList = new HashSet<Movie>();
    String favoriteMovieListJson = preferences.getString("fvt_mv_lst", null);
    if (favoriteMovieListJson != null) {
      favoriteMovieList.addAll((List<Movie>) gson.fromJson(favoriteMovieListJson,
          new TypeToken<List<Movie>>() {
          }.getType()));
      List<Movie> movieList = new ArrayList<Movie>(favoriteMovieList);
      if (movieImageAdapter == null) {
        movieImageAdapter = new MovieImageAdapter(getActivity(), movieList);
        movieGridView.setAdapter(movieImageAdapter);
      } else {
        movieImageAdapter = new MovieImageAdapter(getActivity(), movieList);
        movieGridView.setAdapter(movieImageAdapter);
        movieImageAdapter.setMovieList(movieList);
        movieImageAdapter.notifyDataSetChanged();
      }
    }
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    // Restore the previously serialized activated item position.
    if (savedInstanceState != null
        && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
      mActivatedPosition = savedInstanceState.getInt(STATE_ACTIVATED_POSITION);
      // If we don't need to restart the loader, and there's a desired position to restore
      // to, do so now.
      movieGridView.setSelection(mActivatedPosition);
    }
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
    } else if (id == R.id.action_favorites) {
      sortType = 3;
      //show list of all favorite movies
      getFavoritesMovies();
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (mActivatedPosition != ListView.INVALID_POSITION) {
      outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
    }
    outState.putInt("sortType", sortType);
    outState.putParcelableArrayList("popMoviesList", popularMovieList);
    outState.putParcelableArrayList("topRatedMoviesList", topRatedMovieList);
    outState.putInt("mPosition",mActivatedPosition);
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

  public class GetMovieListTask extends AsyncTask<String, Void, ArrayList<Movie>> {

    private final String LOG_TAG = this.getClass().getSimpleName();

    @Override
    protected ArrayList<Movie> doInBackground(String... params) {
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
    protected void onPostExecute(ArrayList<Movie> movieList) {
      if (movieList != null && !movieList.isEmpty()) {
        if (sortType == MovieSortTypeEnum.SORT_BY_RATING.getId()) {
          topRatedMovieList = movieList;
        } else if (sortType == MovieSortTypeEnum.SORT_BY_POPULARITY.getId()) {
          popularMovieList = movieList;
        }
        if (movieImageAdapter == null) {
          movieImageAdapter = new MovieImageAdapter(getActivity(), movieList);
          movieGridView.setAdapter(movieImageAdapter);
        } else {
          movieImageAdapter.setMovieList(movieList);
          movieImageAdapter.notifyDataSetChanged();
        }
      }
    }

    private ArrayList<Movie> getMovieListFromResponse(String productJSONStr) {
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
