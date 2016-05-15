package com.nanodegree.android.popmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;


public class PopMovieListActivity extends AppCompatActivity
    implements PopMovieListFragment.Callbacks {


  private boolean mTwoPane;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_popmovie_main);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    toolbar.setTitle(getTitle());

    if (findViewById(R.id.popmovie_detail_container) != null) {
      mTwoPane = true;
   //   Toast.makeText(this, "Please select a movie to view Details", Toast.LENGTH_LONG).show();
/*      ((PopMovieListFragment) getSupportFragmentManager()
          .findFragmentById(R.id.popmovie_list))
          .setActivateOnItemClick(true);*/
    } else {
      mTwoPane = false;
    }
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_movie_list, menu);
    return true;
  }

 /* @Override
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
  }*/

  /*private String getApiUrlForList() {
    String SORTING_PARAM = "sort_by";
    String API_KEY = "api_key";
    MovieSortTypeEnum movieSortTypeById = MovieSortTypeEnum.getMovieSortTypeById(sortType);
    Uri.Builder builder = Uri.parse(PopMoviesAppConstants.MOVIE_DB_BASE_URL.concat("discover/movie")).buildUpon();
    builder.appendQueryParameter(SORTING_PARAM, movieSortTypeById.getApiParamName());
    builder.appendQueryParameter(API_KEY, PopMoviesAppConstants.MOVIE_DB_API_KEY);
    if (sortType == MovieSortTypeEnum.SORT_BY_RATING.getId()) {
      //when getting movies sorted by rating, filter movies with considerable # of votes to get relevant data
      builder.appendQueryParameter("vote_count.gte", "100");
    }
    return builder.build().toString();
  }*/


  /**
   * Callback method from {@link PopMovieListFragment.Callbacks}
   * indicating that the item with the given ID was selected.
   */
  @Override
  public void onItemSelected(Movie movie) {
    if (mTwoPane) {
      // In two-pane mode, show the detail view in this activity by
      // adding or replacing the detail fragment using a
      // fragment transaction.
      Bundle arguments = new Bundle();
      arguments.putParcelable("movie", movie);
      arguments.putBoolean("mTwoPane", mTwoPane);
      PopMovieDetailFragment fragment = new PopMovieDetailFragment();
      fragment.setArguments(arguments);
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.popmovie_detail_container, fragment)
          .commit();

    } else {
      // In single-pane mode, simply start the detail activity
      // for the selected item ID.

      Intent intent = new Intent(this, MovieDetailsActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      intent.putExtra("movie_obj", movie);
      intent.putExtra("mTwoPane", mTwoPane);
      startActivity(intent);
    }
  }
}
